/*
 * Copyright (C) 2012 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.zhji.location.app.db.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.location.Geofence;
import com.zhji.location.app.SmartLocationApp;
import com.zhji.location.app.db.DatabaseManager;
import com.zhji.location.app.models.SimpleGeofence;
import com.zhji.location.app.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class LocationDatabase extends BaseDatabase {
    /**
     * Table name
     */
    public static final String TABLE_NAME = "location";

    /**
     * Column name
     */
    public static final String ID = "_id";

    public static final String LOCATION_ID = "location_id";

    public static final String LATITUDE = "latitude";

    public static final String LONGITUDE = "longitude";

    public static final String COUNTRY_NAME = "country_name";

    public static final String COUNTRY_CODE = "country_code";

    public static final String ADDRESS = "address";

    public static final String LOCALITY = "locality";

    public static final String POSTAL_CODE = "postal_code";

    public static final String ACCURACY = "accuracy";

    public static final String CLASSIFICATION = "classification";

    public static final String FREQUENCY = "frequency";

    /**
     * SQL Command: createTable
     */
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + LOCATION_ID + " TEXT PRIMARY_KEY, "
            + LATITUDE + " REAL, " + LONGITUDE + " REAL, " + COUNTRY_NAME + " TEXT, "
            + COUNTRY_CODE + " TEXT, " + ADDRESS + " TEXT, " + LOCALITY + " TEXT, " + POSTAL_CODE
            + " TEXT, " + ACCURACY + " REAL, " + CLASSIFICATION + " INTEGER, " + FREQUENCY
            + " INTEGER);";

    // Max number of the location to create geofences
    private static final int MAX_SIZE = 50;

    // Max number to clean database when the size is reached
    private static final String LIMIT_CLEAN = "20";

    // Error code when the database limit reached
    public static final int LIMIT_REACHED_ERROR = -2;

    /**
     * Constructor method
     * 
     * @param databaseManager
     */
    public LocationDatabase(final DatabaseManager databaseManager) {
        super(databaseManager, TABLE_NAME);
    }

    /**
     * Convenience method to convert location to content values
     * 
     * @param location
     * @return content values
     */
    public static ContentValues toContentValues(final Location location) {
        final ContentValues cv = new ContentValues();
        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();
        cv.put(LOCATION_ID, Utils.generateLocationID(latitude, longitude));
        cv.put(LATITUDE, latitude);
        cv.put(LONGITUDE, longitude);
        cv.put(ACCURACY, location.getAccuracy());
        return cv;
    }

    /**
     * Convenience method to convert address to content values
     * 
     * @param address
     * @return content values
     */
    public static ContentValues toContentValues(final Address address) {
        final ContentValues cv = new ContentValues();
        final double latitude = address.getLatitude();
        final double longitude = address.getLongitude();
        cv.put(LOCATION_ID, Utils.generateLocationID(latitude, longitude));
        cv.put(LATITUDE, latitude);
        cv.put(LONGITUDE, longitude);
        cv.put(COUNTRY_NAME, address.getCountryName());
        cv.put(COUNTRY_CODE, address.getCountryCode());
        cv.put(ADDRESS, address.getAddressLine(0));
        cv.put(LOCALITY, address.getLocality());
        cv.put(POSTAL_CODE, address.getPostalCode());
        return cv;
    }

    /**
     * Convenience method to convert location and address to content values
     * 
     * @param location
     * @param address
     * @return content values
     */
    public static ContentValues toContentValues(final Location location, final Address address) {
        final ContentValues cv = new ContentValues();
        final double latitude = location.getLatitude();
        final double longitude = location.getLongitude();
        cv.put(LOCATION_ID, Utils.generateLocationID(latitude, longitude));
        cv.put(LATITUDE, latitude);
        cv.put(LONGITUDE, longitude);
        cv.put(ACCURACY, location.getAccuracy());
        cv.put(COUNTRY_NAME, address.getCountryName());
        cv.put(COUNTRY_CODE, address.getCountryCode());
        cv.put(ADDRESS, address.getAddressLine(0));
        cv.put(LOCALITY, address.getLocality());
        cv.put(POSTAL_CODE, address.getPostalCode());
        return cv;
    }

    /**
     * Convenience method to generate stub object based on latitude and
     * longitude [for tester]
     * 
     * @param latitude
     * @param longitude
     * @return content values
     */
    public static ContentValues toContentValues(final double latitude, final double longitude) {
        final ContentValues cv = new ContentValues();
        cv.put(LOCATION_ID, Utils.generateLocationID(latitude, longitude));
        cv.put(LATITUDE, latitude);
        cv.put(LONGITUDE, longitude);
        // Generate stub object
        cv.put(ACCURACY, new Random().nextFloat() * 30);

        String countryName = "COUNTRY_NAME";
        String countryCode = "COUNTRY_CODE";
        String address = "ADDRESS";
        String locality = "LOCALITY";
        String postaCode = "POSTAL_CODE";

        final Geocoder geocoder = new Geocoder(SmartLocationApp.getContext(), Locale
                .getDefault());

        if (geocoder.isPresent()) {
            try {
                // Get the first address
                final List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                final Address geoAddress = addresses.get(0);
                countryName = geoAddress.getCountryName();
                countryCode = geoAddress.getCountryCode();
                address = geoAddress.getAddressLine(0);
                locality = geoAddress.getLocality();
                postaCode = geoAddress.getPostalCode();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        cv.put(COUNTRY_NAME, countryName);
        cv.put(COUNTRY_CODE, countryCode);
        cv.put(ADDRESS, address);
        cv.put(LOCALITY, locality);
        cv.put(POSTAL_CODE, postaCode);
        cv.put(FREQUENCY, new Random().nextInt() % 100);
        return cv;
    }

    /**
     * Get frequency of the data with specific locationID
     * 
     * @param locationID
     * @return frequency
     */
    public int getFrequency(final String locationID) {
        final Cursor cursor = query(new String[] {
                FREQUENCY
        }, LOCATION_ID + "=?", new String[] {
                locationID
        }, null, null, null, null);

        if (cursor != null && !cursor.isClosed()) {
            if (cursor.moveToNext()) {
                final int frequency = cursor.getInt(cursor.getColumnIndex(FREQUENCY));
                cursor.close();
                return frequency;
            }
        }

        return 0;
    }

    /**
     * Get id of the data with specific locationID
     * 
     * @param locationID
     * @return id
     */
    public long getID(final String locationID) {
        final Cursor cursor = query(new String[] {
                ID
        }, LOCATION_ID + "=?", new String[] {
                locationID
        }, null, null, null, null);

        if (cursor != null && !cursor.isClosed()) {
            if (cursor.moveToNext()) {
                final long id = cursor.getLong(cursor.getColumnIndex(ID));
                cursor.close();
                return id;
            }
        }

        return 0;
    }

    @Override
    public long insert(final ContentValues values) {
        final String locationID = values.getAsString(LOCATION_ID);
        // Get the frequency of the location
        int frequency = getFrequency(locationID);

        // Checks that have been in place
        if (frequency > 0) {
            // Update the frequency
            values.put(FREQUENCY, ++frequency);

            // Update the location
            update(values, LOCATION_ID + "=?", new String[] {
                    values.getAsString(LOCATION_ID)
            });
            return getID(locationID);
        }

        final int size = getNumberOfRows();

        // If the size more than MAX_SIZE call the method to free some space
        if (size >= MAX_SIZE) {
            return LIMIT_REACHED_ERROR;
        }

        values.put(FREQUENCY, ++frequency);
        return super.insert(values);
    }

    /**
     * This method is used to free database space
     * 
     * @return list of ids removed
     */
    public List<String> freeSpace() {
        final Cursor cursor = query(new String[] {
                ID, LOCATION_ID, FREQUENCY
        }, null, null, null, null, FREQUENCY + " ASC", LIMIT_CLEAN);

        final List<String> ids = new ArrayList<String>();

        if (cursor != null && !cursor.isClosed()) {
            while (cursor.moveToNext()) {
                delete(ID + "=?", new String[] {
                        String.valueOf(cursor.getLong(cursor.getColumnIndex(ID)))
                });
                ids.add(cursor.getString(cursor.getColumnIndex(LOCATION_ID)));
            }
            cursor.close();
        }
        return ids;
    }

    /**
     * Convert the location database to geofences
     * 
     * @return the list of geofences
     */
    public List<Geofence> convertToGeofences() {
        final List<Geofence> geofences = new ArrayList<Geofence>();

        // Get all locations
        final Cursor cursor = query(null, null, null, null, null, null, null);

        if (cursor != null && !cursor.isClosed()) {
            while (cursor.moveToNext()) {
                final String id = cursor.getString(cursor.getColumnIndex(LOCATION_ID));
                final double latitude = cursor.getDouble(cursor.getColumnIndex(LATITUDE));
                final double longitude = cursor.getDouble(cursor.getColumnIndex(LONGITUDE));
                geofences.add(new SimpleGeofence(id + Geofence.GEOFENCE_TRANSITION_ENTER, latitude,
                        longitude,
                        Geofence.GEOFENCE_TRANSITION_ENTER).toGeofence());
                geofences.add(new SimpleGeofence(id + Geofence.GEOFENCE_TRANSITION_EXIT, latitude,
                        longitude,
                        Geofence.GEOFENCE_TRANSITION_EXIT).toGeofence());
            }

            cursor.close();
        }
        return geofences;
    }

    /**
     * Convert the location ids to geofence ids
     * 
     * @param ids
     * @return geofence ids
     */
    public List<String> convertToGeofenceIds(final List<String> ids) {
        final List<String> geofenceIds = new ArrayList<String>();
        for (final String id : ids) {
            geofenceIds.add(id + Geofence.GEOFENCE_TRANSITION_ENTER);
            geofenceIds.add(id + Geofence.GEOFENCE_TRANSITION_EXIT);
        }
        return geofenceIds;
    }
}
