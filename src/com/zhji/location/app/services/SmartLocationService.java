
package com.zhji.location.app.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationStatusCodes;
import com.zhji.location.app.R;
import com.zhji.location.app.db.DatabaseManager;
import com.zhji.location.app.db.model.LocationDatabase;
import com.zhji.location.app.models.SimpleGeofence;
import com.zhji.location.app.utils.GooglePlayServicesUtils;
import com.zhji.location.app.utils.LocationServiceErrorMessages;
import com.zhji.location.app.utils.Utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class SmartLocationService extends Service implements LocationListener, ConnectionCallbacks,
        OnConnectionFailedListener, OnAddGeofencesResultListener, OnRemoveGeofencesResultListener {

    // Tag to debug
    private static final String TAG = SmartLocationService.class.getSimpleName();

    // A request to connect to Location Services
    private LocationRequest mLocationRequest;

    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;

    // Stores the current instantiation of the activity recognition client in
    // this object
    private ActivityRecognitionClient mActivityRecognitionClient;

    // Checks that need to update the location when the GoogleServices connects
    private boolean isStart;

    // Retain last position to know whether it was the same place.
    private Location mLastLocation;

    // List of geofences
    private List<Geofence> mGeofences;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public SmartLocationService getService() {
            // Return this instance of locationService so clients can call
            // public methods
            return SmartLocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate()");

        // Create a new global location parameters object
        mLocationRequest = LocationRequest.create();

        // Set the update interval
        mLocationRequest.setInterval(GooglePlayServicesUtils.LOCATION_UPDATE_INTERVAL);

        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        mLocationRequest.setFastestInterval(GooglePlayServicesUtils.FAST_INTERVAL_CEILING);

        // Create a new location client, using the enclosing class to handle
        // callbacks.
        mLocationClient = new LocationClient(this, this, this);

        // Connect to Google Services
        mLocationClient.connect();

        // Create a new activity recognition client, using the enclosing class
        // to handle callbacks.
        mActivityRecognitionClient = new ActivityRecognitionClient(this,
                this, this);

        // Connect to Google Services
        mActivityRecognitionClient.connect();

        // Start service in foreground
        final Notification.Builder builder =
                new Notification.Builder(this).setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(
                                "location service is running...");

        final Notification notification = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH ? builder
                .build()
                : builder.getNotification();

        notification.flags |= Notification.FLAG_ONGOING_EVENT
                | Notification.FLAG_FOREGROUND_SERVICE | Notification.FLAG_NO_CLEAR;

        startForeground(4321, notification);
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.disconnect();
    }

    /**
     * Start recognition
     */
    public void start() {
        // Checks if Google Services is available
        if (GooglePlayServicesUtils.isGoogleServiceConnected()) {
            if (mLocationClient.isConnected()) {
                mLocationClient.requestLocationUpdates(mLocationRequest, this);
                initGeofences();
            }
            if (mActivityRecognitionClient.isConnected()) {
                initActivityRecognition();
            }
        } else {
            isStart = true;
        }
    }

    /**
     * Stop recognition
     */
    public void stop() {
        mLocationClient.removeLocationUpdates(this);
    }

    @Override
    public void onConnectionFailed(final ConnectionResult result) {
        Log.e(TAG,
                "onConnectionFailed(): "
                        + LocationServiceErrorMessages.getErrorString(result.getErrorCode()));
    }

    @Override
    public void onConnected(final Bundle connectionHint) {
        Log.d(TAG, "onConnected()");
        if (isStart) {
            mLocationClient.requestLocationUpdates(mLocationRequest, this);
            initGeofences();
            initActivityRecognition();
        }
    }

    @Override
    public void onDisconnected() {
        Log.d(TAG, "onDisconnected()");
    }

    @Override
    public void onLocationChanged(final Location location) {
        Log.d(TAG, "onLocationChanged()");
        Log.d(TAG, "Accuracy: " + location.getAccuracy());
        Log.d(TAG, Utils.generateLocationID(location.getLatitude(), location.getLongitude()));

        if (mLastLocation == null
                || mLastLocation.distanceTo(location) > GooglePlayServicesUtils.LOCATION_DISTANCE_MAX) {
            mLastLocation = location;

            // Checks the accuracy
            if (location.getAccuracy() > GooglePlayServicesUtils.LOCATION_DISTANCE_MAX_ACCURACY) {
                Log.d(TAG, "Accuracy is not achieved.");
                return;
            }
            final double latitude = location.getLatitude();
            final double longitude = location.getLongitude();

            // Checks there is location on database
            if (DatabaseManager.getInstance().getLocationDatabase()
                    .getFrequency(Utils.generateLocationID(latitude, longitude)) > 0) {
                Log.d(TAG, "Location already saved id the database.");
                return;
            }

            // Get address and save on database
            AsyncTask.execute(new Runnable() {

                @Override
                public void run() {
                    Log.d(TAG, "Try to get location.");
                    /*
                     * Get a new geocoding service instance, set for localized
                     * addresses. This example uses android.location.Geocoder,
                     * but other geocoders that conform to address standards can
                     * also be used.
                     */
                    final Geocoder geocoder = new Geocoder(getApplicationContext(), Locale
                            .getDefault());

                    // Exit if the Geocoder methods getFromLocation and
                    // getFromLocationName are not implemented
                    if (!geocoder.isPresent()) {
                        Log.d(TAG, "GetFromLocation are not implemented");
                        return;
                    }

                    /*
                     * Call the synchronous getFromLocation() method with the
                     * latitude and longitude of the current location. Return at
                     * most 1 address.
                     */
                    try {
                        final List<Address> addresses = geocoder.getFromLocation(
                                latitude,
                                longitude, 1
                                );

                        // Get the first address
                        final Address address = addresses.get(0);
                        Log.d(TAG,
                                "Save location on database: " + location.getLatitude() + " : "
                                        + location.getLongitude());
                        final StringBuilder text = new StringBuilder();

                        text.append("Latitude: " + location.getLatitude() + "\n");
                        text.append("Longitude: " + location.getLongitude() + "\n");
                        text.append(address.getAddressLine(0));

                        Utils.showNotification("New Location", text.toString(),
                                new Random().nextInt());
                        final LocationDatabase database = DatabaseManager.getInstance()
                                .getLocationDatabase();
                        final ContentValues cv = LocationDatabase.toContentValues(location,
                                address);

                        // Try insert in database, if limit reached then free
                        // some space and try insert again
                        while (database.insert(cv) == LocationDatabase.LIMIT_REACHED_ERROR) {
                            final List<String> geofenceIds = database.freeSpace();
                            mLocationClient.removeGeofences(geofenceIds, SmartLocationService.this);
                        }

                        // Adding new geofences
                        Log.d(TAG, "Adding geofences.");
                        final String geofenceId = Utils.generateLocationID(latitude, longitude);
                        mGeofences.add(new SimpleGeofence(geofenceId
                                + Geofence.GEOFENCE_TRANSITION_ENTER, latitude, longitude,
                                Geofence.GEOFENCE_TRANSITION_ENTER).toGeofence());
                        mGeofences.add(new SimpleGeofence(geofenceId
                                + Geofence.GEOFENCE_TRANSITION_EXIT, latitude, longitude,
                                Geofence.GEOFENCE_TRANSITION_EXIT).toGeofence());
                        updateGeofences();
                        Utils.showNotification("New geofences", geofenceId,
                                new Random().nextInt());
                    } catch (final IOException e) {
                        Log.e(TAG, "Save on database error...");
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * This method load all geofences from database and register on Google Play
     * Service
     */
    private void initGeofences() {
        final LocationDatabase database = DatabaseManager.getInstance()
                .getLocationDatabase();

        if (database != null) {
            mGeofences = database.convertToGeofences();

            if (mGeofences != null && mGeofences.size() > 0) {
                updateGeofences();
            }
        }
    }

    /**
     * Initialize activity recognition
     */
    private void initActivityRecognition() {
        final Intent intent = new Intent(this,
                ActivityRecognitionIntentService.class);
        final PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        mActivityRecognitionClient.requestActivityUpdates(
                GooglePlayServicesUtils.ACTIVITY_UPDATE_INTERVAL,
                pendingIntent);
    }

    /**
     * This method update geofences on location client
     */
    private void updateGeofences() {
        Log.d(TAG, "updateGeofences()");
        // Create an Intent pointing to the IntentService
        final Intent intent = new Intent(getApplicationContext(),
                GeofenceIntentService.class);
        final PendingIntent pendingIntent = PendingIntent.getService(
                getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        mLocationClient.removeGeofences(pendingIntent, this);
        mLocationClient.addGeofences(mGeofences, pendingIntent, this);
    }

    @Override
    public void onAddGeofencesResult(final int statusCode, final String[] geofenceRequestIds) {
        // If adding the geocodes was successful
        if (LocationStatusCodes.SUCCESS == statusCode) {
            Log.d(TAG, "Success. Geocode added.");
        } else {
            Log.e(TAG, "Fail. Geocode did add.");
        }
    }

    @Override
    public void onRemoveGeofencesByPendingIntentResult(final int statusCode,
            final PendingIntent pendingIntent) {
        // If removing the geofences was successful
        if (statusCode == LocationStatusCodes.SUCCESS) {

            // In debug mode, log the result
            Log.d(TAG, "Removing all geofences succeeded.");
        } else {

            // Always log the error
            Log.e(TAG, "Removing all geofences failed: error code " + statusCode);
        }
    }

    @Override
    public void onRemoveGeofencesByRequestIdsResult(final int statusCode,
            final String[] geofenceRequestIds) {
        // If removing the geocodes was successful
        if (LocationStatusCodes.SUCCESS == statusCode) {

            // Log a message containing all the geofence IDs removed.
            Log.d(TAG,
                    "Removing geofences by request ids succeeded: request ids: "
                            + Arrays.toString(geofenceRequestIds));

        } else {
            // If removing the geocodes failed

            // Log a message containing the error code and the list of geofence
            // IDs you tried to remove
            Log.e(TAG, "Removing geofences by request id failed: error code " + statusCode
                    + " GeofenceRequestIds=" + Arrays.toString(geofenceRequestIds));
        }
    }
}
