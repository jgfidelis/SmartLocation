
package com.zhji.location.app.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.zhji.location.app.db.DatabaseManager;
import com.zhji.location.app.db.model.ActivityDatabase;
import com.zhji.location.app.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Random;

public class ActivityRecognitionIntentService extends IntentService {

    private static final String TAG = ActivityRecognitionIntentService.class.getSimpleName();

    private static final int CONFIDENCE_THRESHOLD = 70;

    public ActivityRecognitionIntentService() {
        super(TAG);
    }

    public ActivityRecognitionIntentService(final String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        Log.e(TAG, "onHandleIntent()");

        // If the incoming intent contains an update
        if (ActivityRecognitionResult.hasResult(intent)) {
            // Get the update
            final ActivityRecognitionResult result =
                    ActivityRecognitionResult.extractResult(intent);

            // Get the most probable activity
            final DetectedActivity mostProbableActivity =
                    result.getMostProbableActivity();

            // Get the probability that this activity is the the user's actual
            // activity
            final int confidence = mostProbableActivity.getConfidence();

            // Get an integer describing the type of activity
            final int activityType = mostProbableActivity.getType();

            if (activityType != DetectedActivity.UNKNOWN
                    && activityType != DetectedActivity.TILTING
                    && confidence > CONFIDENCE_THRESHOLD) {

                final String activityName = getNameFromType(activityType);
                final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM hh:mm:ss");
                final String log = String.format(
                        "[" + formatter.format(System.currentTimeMillis())
                                + "] type: %s - conf.: %d", activityName,
                        confidence);
                Log.d(TAG, log);

                // Save on database
                final ActivityDatabase database = DatabaseManager.getInstance()
                        .getActivityDatabase();
                database.insert(ActivityDatabase.toContentValues(activityType));
                Log.d(TAG, "Save activity on database");
                Utils.showNotification("Activity Recognition", log, new Random().nextInt());
            }
        }
    }

    /**
     * Map detected activity types to strings
     * 
     * @param activityType The detected activity type
     * @return A user-readable name for the type
     */
    private static String getNameFromType(final int activityType) {
        switch (activityType) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.UNKNOWN:
                return "unknown";
            case DetectedActivity.TILTING:
                return "tilting";
        }
        return "unknown";
    }
}
