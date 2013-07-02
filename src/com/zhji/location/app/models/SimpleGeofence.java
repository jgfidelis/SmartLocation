
package com.zhji.location.app.models;

import com.google.android.gms.location.Geofence;

public class SimpleGeofence {
    public static float DEFAULT_RADIUS = 30f;
    public static long DEFAULT_EXPIRATION_DURATION = Geofence.NEVER_EXPIRE;

    // Instance variables
    private final String mId;
    private final double mLatitude;
    private final double mLongitude;
    private final float mRadius;
    private final long mExpirationDuration;
    private final int mTransitionType;

    /**
     * The value of parameter is not checked for validity.
     * 
     * @param geofenceId The Geofence's request ID
     * @param latitude Latitude of the Geofence's center.
     * @param longitude Longitude of the Geofence's center.
     * @param radius Radius of the geofence circle.
     * @param expiration Geofence expiration duration in milliseconds.
     * @param transition Type of Geofence transition.
     */
    public SimpleGeofence(
            final String geofenceId,
            final double latitude,
            final double longitude,
            final float radius,
            final long expiration,
            final int transition) {
        // Set the instance fields from the constructor

        // An identifier for the geofence
        mId = geofenceId;

        // Center of the geofence
        mLatitude = latitude;
        mLongitude = longitude;

        // Radius of the geofence, in meters
        mRadius = radius;

        // Expiration time in milliseconds
        mExpirationDuration = expiration;

        // Transition type
        mTransitionType = transition;
    }

    /**
     * The value of parameter is not checked for validity.
     * 
     * @param geofenceId The Geofence's request ID
     * @param latitude Latitude of the Geofence's center.
     * @param longitude Longitude of the Geofence's center.
     * @param transition Type of Geofence transition.
     */
    public SimpleGeofence(
            final String geofenceId,
            final double latitude,
            final double longitude,
            final int transition) {
        // Set the instance fields from the constructor

        // An identifier for the geofence
        mId = geofenceId;

        // Center of the geofence
        mLatitude = latitude;
        mLongitude = longitude;

        // Radius of the geofence, in meters
        mRadius = DEFAULT_RADIUS;

        // Expiration time in milliseconds
        mExpirationDuration = DEFAULT_EXPIRATION_DURATION;

        // Transition type
        mTransitionType = transition;
    }

    // Instance field getters

    /**
     * Get the geofence ID
     * 
     * @return A SimpleGeofence ID
     */
    public String getId() {
        return mId;
    }

    /**
     * Get the geofence latitude
     * 
     * @return A latitude value
     */
    public double getLatitude() {
        return mLatitude;
    }

    /**
     * Get the geofence longitude
     * 
     * @return A longitude value
     */
    public double getLongitude() {
        return mLongitude;
    }

    /**
     * Get the geofence radius
     * 
     * @return A radius value
     */
    public float getRadius() {
        return mRadius;
    }

    /**
     * Get the geofence expiration duration
     * 
     * @return Expiration duration in milliseconds
     */
    public long getExpirationDuration() {
        return mExpirationDuration;
    }

    /**
     * Get the geofence transition type
     * 
     * @return Transition type (see Geofence)
     */
    public int getTransitionType() {
        return mTransitionType;
    }

    /**
     * Creates a Location Services Geofence object from a SimpleGeofence.
     * 
     * @return A Geofence object
     */
    public Geofence toGeofence() {
        // Build a new Geofence object
        return new Geofence.Builder()
                .setRequestId(getId())
                .setTransitionTypes(mTransitionType)
                .setCircularRegion(
                        getLatitude(),
                        getLongitude(),
                        getRadius())
                .setExpirationDuration(mExpirationDuration)
                .build();
    }
}
