
package com.zhji.location.app.views;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.google.android.gms.location.Geofence;
import com.zhji.location.app.R;
import com.zhji.location.app.db.DatabaseManager;
import com.zhji.location.app.db.model.LocationDatabase;
import com.zhji.location.app.services.SmartLocationService;
import com.zhji.location.app.services.SmartLocationService.LocalBinder;

import java.util.List;
import java.util.Random;

public class MainActivity extends Activity implements ServiceConnection {

    private static final String TAG = MainActivity.class.getSimpleName();
    private SmartLocationService mSmartLocationService;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, SmartLocationService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        final Intent intent = new Intent(this, SmartLocationService.class);
        bindService(intent, this, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop() {
        unbindService(this);
        super.onStop();
    }

    public void doSomething(final View view) {
        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {
                final LocationDatabase database = DatabaseManager.getInstance()
                        .getLocationDatabase();
                // final double l = 45;
                // database.insert(LocationDatabase.toContentValues(l, l));

                // database.insert(LocationDatabase.toContentValues(new
                // Random().nextDouble() * 90,
                // new Random().nextDouble() * 90));

                for (int i = 0; i < 50; i++) {
                    database.insert(LocationDatabase.toContentValues(
                            new Random().nextDouble() * 90,
                            new Random().nextDouble() * 90));
                }

                final List<Geofence> geofences = database.convertToGeofences();
                Log.d(TAG, geofences != null ? geofences.toString() : "");
            }
        });
        return;
    }

    @Override
    public void onServiceConnected(final ComponentName name, final IBinder service) {
        final LocalBinder binder = (LocalBinder) service;
        mSmartLocationService = binder.getService();
        mSmartLocationService.startUpdateLocation();
    }

    @Override
    public void onServiceDisconnected(final ComponentName name) {
        // TODO Auto-generated method stub

    }
}
