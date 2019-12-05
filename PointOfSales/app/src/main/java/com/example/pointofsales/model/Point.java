package com.example.pointofsales.model;

public class Point {

    private String mUserId;
    private String mStoreId;
    private int mPoints;

    public String getUserId() {
        return mUserId;
    }
    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getStoreId() {
        return mStoreId;
    }
    public void setStoreId(String storeId) {
        mStoreId = storeId;
    }

    public int getPoints() {
        return mPoints;
    }
    public void setPoints(int points) {
        mPoints = points;
    }
}
