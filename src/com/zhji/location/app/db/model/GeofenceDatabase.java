/*
 * Copyright (C) 2012 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.zhji.location.app.db.model;

import android.content.ContentValues;

import com.google.android.gms.location.Geofence;
import com.zhji.location.app.db.DatabaseManager;

public class GeofenceDatabase extends BaseDatabase {
    /**
     * Table name
     */
    public static final String TABLE_NAME = "geofence";

    /**
     * Column name
     */
    public static final String ID = "_id";

    public static final String LOCATION_ID = "location_id";

    public static final String TIMESTAMP = "timestamp";

    public static final String TRANSITION = "transition";

    /**
     * SQL Command: createTable
     */
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + LOCATION_ID + " TEXT, "
            + TIMESTAMP + " INTEGER, " + TRANSITION + " INTEGER);";

    /**
     * Constructor method
     * 
     * @param databaseManager
     */
    public GeofenceDatabase(final DatabaseManager databaseManager) {
        super(databaseManager, TABLE_NAME);
    }

    /**
     * Convenience method to convert geofence and transition to content values
     * 
     * @param geofence
     * @param transition
     * @return
     */
    public static ContentValues toContentValues(final Geofence geofence, final int transition) {
        final ContentValues cv = new ContentValues();
        final String id = geofence.getRequestId();
        cv.put(LOCATION_ID, id.substring(0, id.length() - 1));
        cv.put(TIMESTAMP, System.currentTimeMillis());
        cv.put(TRANSITION, transition);
        return cv;
    }
}
