package com.example.pointofsales.model;

public class Cart {

    private int mCartQuantity;
    private float mSubtotal;
    private float mDiscount;

    public Cart() {
        mCartQuantity = 0;
        mSubtotal = mDiscount = 0;
    }

    public int getCartQuantity() {
        return mCartQuantity;
    }
    public void setCartQuantity(int cartQuantity) {
        mCartQuantity = cartQuantity;
    }

    public float getSubtotal() {
        return mSubtotal;
    }
    public void setSubtotal(float subtotal) {
        mSubtotal = subtotal;
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
}
