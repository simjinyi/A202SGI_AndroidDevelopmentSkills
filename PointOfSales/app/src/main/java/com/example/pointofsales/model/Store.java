package com.example.pointofsales.model;

public class Store extends User {

    private String mAddress;
    private int mPointsPerPrice;

    public String getAddress() {
        return mAddress;
    }
    public void setAddress(String address) {
        mAddress = address;
    }

    public int getPointsPerPrice() {
        return mPointsPerPrice;
    }
    public void setPointsPerPrice(int pointsPerPrice) {
        mPointsPerPrice = pointsPerPrice;
    }
}
