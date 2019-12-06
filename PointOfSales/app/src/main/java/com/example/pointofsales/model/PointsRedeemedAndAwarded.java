package com.example.pointofsales.model;

/**
 * Model class to store the temporary points redeemed and awarded prior to insertion into database
 */
public class PointsRedeemedAndAwarded {

    private int mRedeemedPoint;
    private int mPointAwarded;

    public PointsRedeemedAndAwarded() {
        mRedeemedPoint = mPointAwarded = 0;
    }

    public PointsRedeemedAndAwarded(int redeemedPoint, int pointAwarded) {
        mRedeemedPoint = redeemedPoint;
        mPointAwarded = pointAwarded;
    }

    public int getRedeemedPoint() {
        return mRedeemedPoint;
    }

    public void setRedeemedPoint(int redeemedPoint) {
        mRedeemedPoint = redeemedPoint;
    }

    public int getPointAwarded() {
        return mPointAwarded;
    }

    public void setPointAwarded(int pointAwarded) {
        mPointAwarded = pointAwarded;
    }
}
