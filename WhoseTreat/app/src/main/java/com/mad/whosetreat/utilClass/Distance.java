package com.mad.whosetreat.utilClass;

/**
 * adapted for WhoseTreat based on the code at
 * https://stackoverflow.com/questions/14799998/how-to-find-distance-using-google-places-api
 * <p>
 * <p>
 * Distance using Haversine formula to calculate distance in meters
 */
public class Distance {

    // fields
    private double mLatOrigin;
    private double mLngOrigin;
    private double mLatDest;
    private double mLngDest;

    /**
     * constructor sets the fields
     */
    public Distance(double mLatOrigin, double mLngOrigin, double mLatDest, double mLngDest) {
        this.mLatOrigin = mLatOrigin;
        this.mLngOrigin = mLngOrigin;
        this.mLatDest = mLatDest;
        this.mLngDest = mLngDest;
    }


    /**
     * calculate distance using Haversine formula baed on the fields and return the result as integer (meter)
     * @return
     */
    public int getDistance() {

        double earthRadius = 6371000; // result will be meter scaled
        double dLat = Math.toRadians(mLatDest - mLatOrigin);
        double dLng = Math.toRadians(mLngDest - mLngOrigin);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(mLatOrigin)) * Math.cos(Math.toRadians(mLatDest));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (int) (earthRadius * c);
    }
}

