
package com.zhji.location.app.services;

import android.app.Notification;
import android.app.Service;
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
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.zhji.location.app.R;
import com.zhji.location.app.db.DatabaseManager;
import com.zhji.location.app.db.model.LocationDatabase;
import com.zhji.location.app.utils.GooglePlayServicesUtils;
import com.zhji.location.app.utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SmartLocationService extends Service implements LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    // Tag to debug
    private static final String TAG = SmartLocationService.class.getSimpleName();

    // A request to connect to Location Services
    private LocationRequest mLocationRequest;

    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;

    // Checks that need to update the location when the GoogleServices connects
    private boolean isUpdatedLocation;

    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    private Location mLastLocation;

    public class LocalBinder extends Binder {
        public SmartLocationService getService() {
            // Return this instance of locationService so clients can call
            // public
            // methods
            return SmartLocationService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Create a new global location parameters object
        mLocationRequest = LocationRequest.create();

        // Set the update interval
        mLocationRequest.setInterval(GooglePlayServicesUtils.UPDATE_INTERVAL);

        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        mLocationRequest.setFastestInterval(GooglePlayServicesUtils.FAST_INTERVAL_CEILING);

        // Create a new location client, using the enclosing class to handle
        // callbacks.
        mLocationClient = new LocationClient(this, this, this);

        // Connect to Google Services
        mLocationClient.connect();

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

        startForeground(4433, notification);
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

    public void startUpdateLocation() {
        // Checks if Google Services is available
        if (GooglePlayServicesUtils.isGoogleServiceConnected() && mLocationClient.isConnected()) {
            mLocationClient.requestLocationUpdates(mLocationRequest, this);
        } else {
            isUpdatedLocation = true;
        }
    }

    public void stopUpdateLocation() {
        mLocationClient.removeLocationUpdates(this);
    }

    @Override
    public void onConnectionFailed(final ConnectionResult result) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onConnected(final Bundle connectionHint) {
        if (isUpdatedLocation) {
            mLocationClient.requestLocationUpdates(mLocationRequest, this);
        }
    }

    @Override
    public void onDisconnected() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onLocationChanged(final Location location) {
        // Log.d(TAG, location.getLatitude() + " : " + location.getLongitude());
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
                    /*
                     * Get a new geocoding service instance, set for localized
                     * addresses. This example uses android.location.Geocoder,
                     * but other geocoders that conform to address standards can
                     * also be used.
                     */
                    final Geocoder geocoder = new Geocoder(getApplicationContext(), Locale
                            .getDefault());

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
                                "Save on database: " + location.getLatitude() + " : "
                                        + location.getLongitude());
                        DatabaseManager.getInstance().getLocationDatabase()
                                .insert(LocationDatabase.toContentValues(location,
                                        address));
                    } catch (final IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
