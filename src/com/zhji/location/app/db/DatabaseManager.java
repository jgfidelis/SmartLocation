
package com.zhji.location.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zhji.location.app.SmartLocationApp;
import com.zhji.location.app.db.model.ActivityDatabase;
import com.zhji.location.app.db.model.GeofenceDatabase;
import com.zhji.location.app.db.model.LocationDatabase;

public class DatabaseManager extends SQLiteOpenHelper {

    private final ActivityDatabase mActivityDatabase;
    private final GeofenceDatabase mGeofencingDatabase;
    private final LocationDatabase mLocationDatabase;

    /**
     * Database name
     */
    private static final String DB_NAME = "database";

    /**
     * Database version
     */
    private static final int DB_VERSION = 1;

    /**
     * SingletonHolder is loaded on the first execution of
     * Singleton.getInstance() or the first access to SingletonHolder.INSTANCE,
     * not before.
     */
    private static class SingletonHolder {
        public static final DatabaseManager INSTANCE = new DatabaseManager(
                SmartLocationApp.getContext());
    }

    public static DatabaseManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public DatabaseManager(final Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mActivityDatabase = new ActivityDatabase(this);
        mGeofencingDatabase = new GeofenceDatabase(this);
        mLocationDatabase = new LocationDatabase(this);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        // Create the tables
        db.execSQL(ActivityDatabase.CREATE_TABLE);
        db.execSQL(GeofenceDatabase.CREATE_TABLE);
        db.execSQL(LocationDatabase.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        if (newVersion > oldVersion) {
            // Drop the tables
            db.execSQL("DROP TABLE IF EXISTS " + ActivityDatabase.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + GeofenceDatabase.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + LocationDatabase.TABLE_NAME);
            onCreate(db);
        }
    }

    @Override
    public void onDowngrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        if (newVersion < oldVersion) {
            // Drop the tables
            db.execSQL("DROP TABLE IF EXISTS " + ActivityDatabase.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + GeofenceDatabase.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + LocationDatabase.TABLE_NAME);
            onCreate(db);
        }
    }

    /**
     * @return the mActivityDatabase
     */
    public ActivityDatabase getActivityDatabase() {
        return mActivityDatabase;
    }

    /**
     * @return the mGeofencingDatabase
     */
    public GeofenceDatabase getGeofencingDatabase() {
        return mGeofencingDatabase;
    }

    /**
     * @return the mLocationDatabase
     */
    public LocationDatabase getLocationDatabase() {
        return mLocationDatabase;
    }
}
