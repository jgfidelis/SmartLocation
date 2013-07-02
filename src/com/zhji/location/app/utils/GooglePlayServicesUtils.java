
package com.zhji.location.app.utils;

import android.location.Location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.zhji.location.app.SmartLocationApp;

public final class GooglePlayServicesUtils {
    // The update interval in milliseconds
    public static final int UPDATE_INTERVAL = 5000;

    // A fast interval ceiling in milliseconds
    public static final int FAST_INTERVAL_CEILING = 1000;

    // Maximum distance to upgrade a new location
    public static final float LOCATION_DISTANCE_MAX = 30;

    // Maximum accuracy distance to upgrade a new location
    public static final float LOCATION_DISTANCE_MAX_ACCURACY = 30;

    /**
     * Verify that Google Play services is available before making a request.
     * 
     * @return true if Google Play services is available, otherwise false
     */
    public static boolean isGoogleServiceConnected() {

        // Check that Google Play services is available
        final int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(SmartLocationApp.getContext());

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        } else {
            // Google Play services was not available for some reason
            return false;
        }
    }

    /**
     * Computes the approximate distance in meters between two locations, and
     * optionally the initial and final bearings of the shortest path between
     * them. Distance and bearing are defined using the WGS84 ellipsoid.
     * 
     * @param startLatitude the starting latitude
     * @param startLongitude the starting longitude
     * @param endLatitude the ending latitude
     * @param endLongitude the ending longitude
     * @return distance between two locations
     */
    public static final float distanceBetween(final double startLatitude,
            final double startLongitude, final double endLatitude, final double endLongitude) {
        final float[] results = new float[5];
        Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);
        return results[0];
    }
}
