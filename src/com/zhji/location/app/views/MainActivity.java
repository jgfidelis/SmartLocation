
package com.zhji.location.app.views;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.zhji.location.app.R;
import com.zhji.location.app.db.DatabaseManager;
import com.zhji.location.app.db.model.ActivityDatabase;
import com.zhji.location.app.db.model.GeofenceDatabase;
import com.zhji.location.app.db.model.LocationDatabase;
import com.zhji.location.app.services.SmartLocationService;
import com.zhji.location.app.services.SmartLocationService.LocalBinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements ServiceConnection {

    /*
     * Receiver to update the google map
     */
    public static class GoogleMapsUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            Log.d(TAG, "GoogleMapsUpdateReceiver onReceive()");

            Log.d(TAG, "Action: " + intent.getAction());
            if (intent.getAction().equals(SmartLocationService.ADD_NEW_LOCATION_ACTION)) {
                final String locationId = intent
                        .getStringExtra(SmartLocationService.ADD_NEW_LOCATION_KEY);
                if (locationId != null) {
                    // TODO Adds new location on the map
                    Log.d(TAG, "Update maps: " + locationId);
                }
            } else if (intent.getAction().equals(SmartLocationService.REMOVE_LOCATION_ACTION)) {
                final ArrayList<String> ids = intent
                        .getStringArrayListExtra(SmartLocationService.REMOVE_LOCATION_KEY);
                if (ids != null) {
                    // TODO Removes locations of the map
                    Log.d(TAG, "Remove maps: " + ids);
                }
            }
        }
    }

    private static final String TAG = MainActivity.class.getSimpleName();
    protected static final int MAX_NUM_LOCATION_STUB = 40;
    protected static final int MAX_NUM_OF_DAY_GEOFENCE_STUB = 15;
    protected static final int MAX_NUM_OF_DAY_ACTIVITY_STUB = 2;
    protected static final float GEOFENCE_MUTATION_ERROR = 0.95f;
    private SmartLocationService mSmartLocationService;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "Starting Smart Location Service...");
        startService(new Intent(this, SmartLocationService.class));
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
    public void onServiceConnected(final ComponentName name, final IBinder service) {
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
                Toast.makeText(this, "Settings not implemented.", Toast.LENGTH_SHORT).show();
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
                    latitude = -(22.85 + new Random().nextDouble() * 0.1);
                    longitude = -(46.95 + new Random().nextDouble() * 0.1);
                    database.insert(LocationDatabase.toContentValues(latitude, longitude));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(MainActivity.this, "Generating stub location done!",
                                Toast.LENGTH_SHORT)
                                .show();
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
                final LocationDatabase locationDatabase = DatabaseManager.getInstance()
                        .getLocationDatabase();
                final GeofenceDatabase database = DatabaseManager.getInstance()
                        .getGeofencingDatabase();
                final List<Geofence> geofences = locationDatabase.convertToGeofences();
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
                                    geofences.get(location),
                                    timestamp, Geofence.GEOFENCE_TRANSITION_ENTER));
                            timestamp += new Random().nextInt(480) * 60000;
                        } while (new Random().nextFloat() > GEOFENCE_MUTATION_ERROR);

                        do {
                            database.insert(GeofenceDatabase.toContentValues(
                                    geofences.get(location),
                                    timestamp, Geofence.GEOFENCE_TRANSITION_EXIT));

                            timestamp += new Random().nextInt(120) * 60000;
                        } while (new Random().nextFloat() > GEOFENCE_MUTATION_ERROR);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Generating stub geofence done!",
                                Toast.LENGTH_SHORT)
                                .show();
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
                    database.insert(ActivityDatabase.toContentValues(new Random().nextInt(4),
                            timestamp));
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
        Toast.makeText(this, "Cleaning all database...", Toast.LENGTH_SHORT).show();
        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {
                final DatabaseManager databaseManager = DatabaseManager.getInstance();
                databaseManager.getLocationDatabase().delete(null, null);
                databaseManager.getGeofencingDatabase().delete(null, null);
                databaseManager.getActivityDatabase().delete(null, null);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Cleaning all database done!",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
