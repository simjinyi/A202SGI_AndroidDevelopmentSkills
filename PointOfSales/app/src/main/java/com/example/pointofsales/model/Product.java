package com.example.pointofsales.model;

import android.graphics.Bitmap;

public class Product {

    private String mId;
    private Bitmap mImage;
    private String mName;
    private float mPrice;
    private int mPointPerItem;
    private int mInventoryQuantity;

    private int mTotalSales;
    private boolean mIsDisabled;
    private int mCartQuantity;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        if (id != null)
            mId = id;
    }

    public Bitmap getImage() {
        return mImage;
    }

    public void setImage(Bitmap image) {
        mImage = image;
    }

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
        if (price > 0)
            mPrice = price;
    }

    public int getPointPerItem() {
        return mPointPerItem;
    }

    public void setPointPerItem(int pointPerItem) {
        if (pointPerItem >= 0)
            mPointPerItem = pointPerItem;
    }

    public int getInventoryQuantity() {
        return mInventoryQuantity;
    }

    public void setInventoryQuantity(int inventoryQuantity) {
        if (inventoryQuantity >= 0)
            mInventoryQuantity = inventoryQuantity;
    }

    public int getTotalSales() {
        return mTotalSales;
    }

    public void setTotalSales(int totalSales) {
        mTotalSales = totalSales;
    }

    public boolean isDisabled() {
        return mIsDisabled;
    }

    public void setDisabled(boolean disabled) {
        mIsDisabled = disabled;
    }

    public int getCartQuantity() {
        return mCartQuantity;
    }

    public void setCartQuantity(int cartQuantity) {
        if (cartQuantity >= 0)
            mCartQuantity = cartQuantity;
    }
}
