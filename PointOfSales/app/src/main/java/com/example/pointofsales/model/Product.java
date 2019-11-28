package com.example.pointofsales.model;

import android.graphics.Bitmap;

public class Product {

    private Bitmap image;
    private String name;
    private float price;
    private int pointPerItem;
    private int inventoryQuantity;

    private int totalSales;
    private boolean isDisabled;
    private int cartQuantity;

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        if (price > 0)
            this.price = price;
    }

    public int getPointPerItem() {
        return pointPerItem;
    }

    public void setPointPerItem(int pointPerItem) {
        if (pointPerItem >= 0)
            this.pointPerItem = pointPerItem;
    }

    public int getInventoryQuantity() {
        return inventoryQuantity;
    }

    public void setInventoryQuantity(int inventoryQuantity) {
        if (inventoryQuantity >= 0)
            this.inventoryQuantity = inventoryQuantity;
    }

    public int getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(int totalSales) {
        this.totalSales = totalSales;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
    }

    public int getCartQuantity() {
        return cartQuantity;
    }

    public void setCartQuantity(int cartQuantity) {
        if (cartQuantity >= 0)
            this.cartQuantity = cartQuantity;
    }
}
