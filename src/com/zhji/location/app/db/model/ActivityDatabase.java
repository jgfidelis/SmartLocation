/*
 * Copyright (C) 2012 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.zhji.location.app.db.model;

import android.content.ContentValues;

import com.zhji.location.app.db.DatabaseManager;

public class ActivityDatabase extends BaseDatabase {
    /**
     * Table name
     */
    public static final String TABLE_NAME = "activity";

    /**
     * Column name
     */
    public static final String ID = "_id";

    public static final String TIMESTAMP = "timestamp";

    public static final String ACTIVITY = "activity";

    /**
     * SQL Command: createTable
     */
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TIMESTAMP + " INTEGER, " + ACTIVITY
            + " INTEGER);";

    /**
     * Constructor method
     * 
     * @param databaseManager
     */
    public ActivityDatabase(final DatabaseManager databaseManager) {
        super(databaseManager, TABLE_NAME);
    }

    /**
     * Convenience method to convert activityType to content values
     * 
     * @param activityType
     * @return content values
     */
    public static ContentValues toContentValues(final int activityType) {
        final ContentValues cv = new ContentValues();
        cv.put(ACTIVITY, activityType);
        cv.put(TIMESTAMP, System.currentTimeMillis());
        return cv;
    }

    /**
     * Convenience method to convert activityType to content values
     * 
     * @param activityType
     * @param timestamp
     * @return content values
     */
    public static ContentValues toContentValues(final int activityType, final long timestamp) {
        final ContentValues cv = new ContentValues();
        cv.put(ACTIVITY, activityType);
        cv.put(TIMESTAMP, timestamp);
        return cv;
    }
}
