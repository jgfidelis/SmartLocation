/*
 * Copyright (C) 2012 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.zhji.location.app.db.model;

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

    public static final String LOCATION_ID = "location_id";

    public static final String TIMESTAMP = "timestamp";

    public static final String ACTIVITY = "activity";

    /**
     * SQL Command: createTable
     */
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + LOCATION_ID + " INTEGER, " + TIMESTAMP
            + " INTEGER, " + ACTIVITY + " INTEGER);";

    /**
     * Constructor method
     * 
     * @param databaseManager
     */
    public ActivityDatabase(final DatabaseManager databaseManager) {
        super(databaseManager, TABLE_NAME);
    }
}
