package com.example.pointofsales.model;

/**
 * Model class to store the transaction items composited by the Transaction class
 */
public class TransactionItem {

    private String mName;
    private float mPrice;
    private int mQuantity;

    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }

    public float getPrice() {
        return mPrice;
    }
    public void setPrice(float price) {
        mPrice = price;
    }

    public int getQuantity() {
        return mQuantity;
    }
    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }

    public float getExtension() {
        return mPrice * mQuantity;
    }
}
