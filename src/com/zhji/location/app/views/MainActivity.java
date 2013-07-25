
package com.zhji.location.app.views;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.zhji.location.app.R;
import com.zhji.location.app.db.DatabaseManager;
import com.zhji.location.app.db.model.ActivityDatabase;
import com.zhji.location.app.db.model.GeofenceDatabase;
import com.zhji.location.app.db.model.LocationDatabase;
import com.zhji.location.app.models.SimpleGeofence;
import com.zhji.location.app.services.SmartLocationService;
import com.zhji.location.app.services.SmartLocationService.LocalBinder;

import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements ServiceConnection {

    /*
     * Receiver to update the google map
     */
    public class GoogleMapsUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (intent.getAction().equals(
                    SmartLocationService.ADD_NEW_LOCATION_ACTION)) {
                final String locationId = intent
                        .getStringExtra(SmartLocationService.ADD_NEW_LOCATION_KEY);
                if (locationId != null) {
                    final LocationDatabase database = DatabaseManager.getInstance()
                            .getLocationDatabase();
                    final Cursor cursor = database.query(null, LocationDatabase.LOCATION_ID + "=?",
                            new String[] {
                                locationId
                            }, null, null, null, null);

                    // Add new marker on the map
                    if (cursor != null) {
                        addMarker(cursor);
                        cursor.close();
                    }
                }
            } else if (intent.getAction().equals(
                    SmartLocationService.REMOVE_LOCATION_ACTION)) {
                rebuildMap();
            }
        }
    }

    private static final String TAG = MainActivity.class.getSimpleName();
    protected static final int MAX_NUM_LOCATION_STUB = 40;
    protected static final int MAX_NUM_OF_DAY_GEOFENCE_STUB = 15;
    protected static final int MAX_NUM_OF_DAY_ACTIVITY_STUB = 2;
    protected static final float GEOFENCE_MUTATION_ERROR = 0.95f;
    private SmartLocationService mSmartLocationService;
    private static GoogleMap mMap;
    private GoogleMapsUpdateReceiver mMapReceiver;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        Log.d(TAG, "Starting Smart Location Service...");
        startService(new Intent(this, SmartLocationService.class));

        // Register broadcast
        mMapReceiver = new GoogleMapsUpdateReceiver();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(SmartLocationService.ADD_NEW_LOCATION_ACTION);
        filter.addAction(SmartLocationService.REMOVE_LOCATION_ACTION);
        registerReceiver(mMapReceiver, filter);

        // Load map from database
        loadMapfromDB();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        final Intent intent = new Intent(this, SmartLocationService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);
        Log.d(TAG, "Bind service...");
    }

    @Override
    protected void onStop() {
        unbindService(this);
        Log.d(TAG, "Unbind service...");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister broadcast to the map
        unregisterReceiver(mMapReceiver);
    }

    @Override
    public void onServiceConnected(final ComponentName name,
            final IBinder service) {
        final LocalBinder binder = (LocalBinder) service;
        mSmartLocationService = binder.getService();
        mSmartLocationService.start();
        Log.d(TAG, "Smart Location Service connected...");
    }

    @Override
    public void onServiceDisconnected(final ComponentName name) {
        Log.d(TAG, "Smart Location Service disconnected...");
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // Send broadcast with new id.
                Toast.makeText(this, "Settings not implemented.",
                        Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_generate_stub_location:
                generateStubLocation();
                break;
            case R.id.action_generate_stub_geofence:
                generateStubGeofence();
                break;
            case R.id.action_generate_stub_activity:
                generateStubActivityRecognition();
                break;
            case R.id.action_generate_stub_all:
                generateStubLocation();
                generateStubGeofence();
                generateStubActivityRecognition();
                break;
            case R.id.action_clear_all_database:
                clearAllDatabase();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method populate the location database with stub data
     */
    private void generateStubLocation() {
        Toast.makeText(this, "Generating stub location...", Toast.LENGTH_SHORT)
                .show();
        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {
                final LocationDatabase database = DatabaseManager.getInstance()
                        .getLocationDatabase();

                double latitude;
                double longitude;

                for (int i = 0; i < MAX_NUM_LOCATION_STUB; i++) {
                    latitude = -(22.87 + new Random().nextDouble() * 0.1);
                    longitude = -(47 + new Random().nextDouble() * 0.1);
                    database.insert(LocationDatabase.toContentValues(latitude,
                            longitude));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadMapfromDB();
                        Toast.makeText(MainActivity.this,
                                "Generating stub location done!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * This method populate the geofence database with stub data
     */
    private void generateStubGeofence() {
        Toast.makeText(this, "Generating stub geofence...", Toast.LENGTH_SHORT)
                .show();
        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {
                final LocationDatabase locationDatabase = DatabaseManager
                        .getInstance().getLocationDatabase();
                final GeofenceDatabase database = DatabaseManager.getInstance()
                        .getGeofencingDatabase();
                final List<Geofence> geofences = locationDatabase
                        .convertToGeofences();
                final int size = geofences.size();
                if (geofences != null && size > 0) {
                    int location;
                    final long currentTime = System.currentTimeMillis();
                    long timestamp = currentTime - AlarmManager.INTERVAL_DAY
                            * MAX_NUM_OF_DAY_GEOFENCE_STUB;
                    while (timestamp < currentTime) {
                        location = new Random().nextInt(size);
                        do {
                            database.insert(GeofenceDatabase.toContentValues(
                                    geofences.get(location), timestamp,
                                    Geofence.GEOFENCE_TRANSITION_ENTER));
                            timestamp += new Random().nextInt(480) * 60000;
                        } while (new Random().nextFloat() > GEOFENCE_MUTATION_ERROR);

                        do {
                            database.insert(GeofenceDatabase.toContentValues(
                                    geofences.get(location), timestamp,
                                    Geofence.GEOFENCE_TRANSITION_EXIT));

                            timestamp += new Random().nextInt(120) * 60000;
                        } while (new Random().nextFloat() > GEOFENCE_MUTATION_ERROR);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,
                                "Generating stub geofence done!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * This method populate the activity recognition database with stub data
     */
    private void generateStubActivityRecognition() {
        Toast.makeText(this, "Generating stub activity recognition...",
                Toast.LENGTH_SHORT).show();
        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {
                final ActivityDatabase database = DatabaseManager.getInstance()
                        .getActivityDatabase();
                final long currentTime = System.currentTimeMillis();
                long timestamp = currentTime - AlarmManager.INTERVAL_DAY
                        * MAX_NUM_OF_DAY_ACTIVITY_STUB;
                while (timestamp < currentTime) {
                    database.insert(ActivityDatabase.toContentValues(
                            new Random().nextInt(4), timestamp));
                    timestamp += new Random().nextInt(5) * 60000;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,
                                "Generating stub activity recognition done!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * This method clear all database
     */
    private void clearAllDatabase() {
        Toast.makeText(this, "Cleaning all database...", Toast.LENGTH_SHORT)
                .show();
        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {
                final DatabaseManager databaseManager = DatabaseManager
                        .getInstance();
                databaseManager.getLocationDatabase().delete(null, null);
                databaseManager.getGeofencingDatabase().delete(null, null);
                databaseManager.getActivityDatabase().delete(null, null);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,
                                "Cleaning all database done!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * The marker's icon is rendered on the map at the location Marker.position
     * 
     * @param cursor
     */
    private void addMarker(final Cursor cursor) {

        final double latitude = cursor.getDouble(cursor.getColumnIndex(LocationDatabase.LATITUDE));
        final double longitude = cursor
                .getDouble(cursor.getColumnIndex(LocationDatabase.LONGITUDE));
        final String address = cursor.getString(cursor.getColumnIndex(LocationDatabase.ADDRESS));

        final LatLng point = new LatLng(latitude, longitude);

        final MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.title(address);
        mMap.addMarker(markerOptions);

        final CircleOptions circle = new CircleOptions();
        circle.center(point);
        circle.fillColor(0x40ff0000);
        circle.radius(SimpleGeofence.DEFAULT_RADIUS);
        circle.strokeWidth(2);
        mMap.addCircle(circle);

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 14f));
    }

    /**
     * Load the location from DB and show on the maps.
     */
    private void loadMapfromDB() {

        final LocationDatabase ldb = DatabaseManager.getInstance().getLocationDatabase();

        final Cursor cursor = ldb.query(null, null, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                addMarker(cursor);
            }
            cursor.close();
        }
    }

    /**
     * This method rebuild the maps when receive a broadcast to remove some
     * location
     */
    private void rebuildMap() {
        mMap.clear();
        loadMapfromDB();
    }
}
