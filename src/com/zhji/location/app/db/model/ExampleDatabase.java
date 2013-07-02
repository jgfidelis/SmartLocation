/*
 * Copyright (C) 2012 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.zhji.location.app.db.model;

import com.zhji.location.app.db.DatabaseManager;

public class ExampleDatabase extends BaseDatabase {
    /**
     * Table name
     */
    public static final String TABLE_NAME = "example";

    /**
     * Column name
     */
    public static final String ID = "_id";

    public static final String NAME = "name";

    public static final String TYPE = "type";

    public static final String START = "start";

    public static final String END = "end";

    public static final String LOCATIONS = "locations";

    public static final String PHOTO_DATA = "photoData";

    public static final String VIDEO_DATA = "videoData";

    public static final String MUSIC_DATA = "musicData";

    public static final String FRIENDS = "friends";

    public static final String SHARED = "shared";

    /**
     * SQL Command: createTable
     */
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT, " + TYPE + " INTEGER, "
            + START + " INTEGER, " + END + " INTEGER, " + LOCATIONS + " BLOB, " + PHOTO_DATA
            + " BLOB, " + VIDEO_DATA + " BLOB, " + MUSIC_DATA + " BLOB, " + FRIENDS + " BLOB, "
            + SHARED + " INTEGER);";

    /**
     * Constructor method
     * 
     * @param databaseManager
     */
    public ExampleDatabase(final DatabaseManager databaseManager) {
        super(databaseManager, TABLE_NAME);
    }
}
