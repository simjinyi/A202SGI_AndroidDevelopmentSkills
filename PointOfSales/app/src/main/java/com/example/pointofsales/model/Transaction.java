package com.example.pointofsales.model;

import java.util.ArrayList;

public class Transaction {

    private String mUserName;
    private String mUserId;
    private String mStoreName;
    private String mStoreId;

    private long mTimestamp;
    private float mSubtotal;
    private Integer mPointsRedeemed;
    private Integer mPointsAwarded;
    private float mDiscount;

    private ArrayList<TransactionItem> mTransactionItems;

    public String getStoreName() {
        return mStoreName;
    }
    public void setStoreName(String storeName) {
        mStoreName = storeName;
    }

    public String getUserName() {
        return mUserName;
    }
    public void setUserName(String userName) {
        mUserName = userName;
    }

    public long getTimestamp() {
        return mTimestamp;
    }
    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }

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

    public float getSubtotal() {
        return mSubtotal;
    }
    public void setSubtotal(float subtotal) {
        mSubtotal = subtotal;
    }

    public Integer getPointsRedeemed() {
        return mPointsRedeemed;
    }
    public void setPointsRedeemed(Integer pointsRedeemed) {
        mPointsRedeemed = pointsRedeemed;
    }

    public Integer getPointsAwarded() {
        return mPointsAwarded;
    }
    public void setPointsAwarded(Integer pointsAwarded) {
        mPointsAwarded = pointsAwarded;
    }

    public float getDiscount() {
        return mDiscount;
    }
    public void setDiscount(float discount) {
        mDiscount = discount;
    }

    public float getTotal() {
        return mSubtotal - mDiscount;
    }
    public int getPointsPerPrice() { return (int) (mPointsRedeemed / mDiscount); }

    public ArrayList<TransactionItem> getTransactionItems() {
        return mTransactionItems;
    }
    public void setTransactionItems(ArrayList<TransactionItem> transactionItems) {
        mTransactionItems = transactionItems;
    }
}
