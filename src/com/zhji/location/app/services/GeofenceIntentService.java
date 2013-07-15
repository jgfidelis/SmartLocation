
package com.zhji.location.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.zhji.location.app.R;
import com.zhji.location.app.db.DatabaseManager;
import com.zhji.location.app.db.model.GeofenceDatabase;
import com.zhji.location.app.utils.LocationServiceErrorMessages;
import com.zhji.location.app.utils.Utils;

import java.util.List;
import java.util.Random;

public class GeofenceIntentService extends IntentService {

    private static final String TAG = GeofenceIntentService.class.getSimpleName();

    public GeofenceIntentService() {
        super(TAG);
    }

    public GeofenceIntentService(final String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        Log.e(TAG, "onHandleIntent()");
        if (LocationClient.hasError(intent)) {
            // Get the error code
            final int errorCode = LocationClient.getErrorCode(intent);

            // Get the error message
            final String errorMessage = LocationServiceErrorMessages.getErrorString(errorCode);

            // Log the error
            Log.e(TAG, getString(R.string.geofence_transition_error_detail, errorMessage));
            Utils.showNotification("Geofences Error",
                    getString(R.string.geofence_transition_error_detail, errorMessage),
                    new Random().nextInt());
        } else {
            Log.e(TAG, "Geofence enter or exit.");

            // Get the type of transition (enter or exit)
            final int transition = LocationClient.getGeofenceTransition(intent);
            final List<Geofence> geofences = LocationClient.getTriggeringGeofences(intent);
            final GeofenceDatabase database = DatabaseManager.getInstance().getGeofencingDatabase();

            for (final Geofence geofence : geofences) {
                Log.d(TAG, "Save geofences on database");
                Utils.showNotification("Geofence "
                        + (transition == Geofence.GEOFENCE_TRANSITION_ENTER ? "enter" : "exit"),
                        geofence.getRequestId(),
                        new Random().nextInt());
                database.insert(GeofenceDatabase.toContentValues(geofence, transition));
            }
        }
    }
}
