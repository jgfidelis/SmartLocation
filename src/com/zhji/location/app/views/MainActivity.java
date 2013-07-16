
package com.zhji.location.app.views;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.zhji.location.app.R;
import com.zhji.location.app.services.SmartLocationService;
import com.zhji.location.app.services.SmartLocationService.LocalBinder;

public class MainActivity extends Activity implements ServiceConnection {

    private static final String TAG = MainActivity.class.getSimpleName();
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
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method populate the location database with stub data
     */
    private void generateStubLocation() {
        // TODO Auto-generated method stub
        Toast.makeText(this, "Generate stub location not implemented.", Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * This method populate the geofence database with stub data
     */
    private void generateStubGeofence() {
        // TODO Auto-generated method stub
        Toast.makeText(this, "Generate stub geofence not implemented.", Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * This method populate the activity recognition database with stub data
     */
    private void generateStubActivityRecognition() {
        // TODO Auto-generated method stub
        Toast.makeText(this, "Generate stub activity recognition not implemented.",
                Toast.LENGTH_SHORT).show();
    }
}
