package com.example.pointofsales.model;

public class Cart {

    private Product mProduct;

    private int mCartQuantity;
    private float mCartExtension;

    public Cart(Product product) {
        mProduct = product;
        mCartQuantity = 0;
        mCartExtension = 0;
    }

    public Product getProduct() {
        return mProduct;
    }
    public void setProduct(Product product) {
        mProduct = product;
    }

    public int getCartQuantity() {
        return mCartQuantity;
    }
    public void setCartQuantity(int cartQuantity) {
        mCartQuantity = cartQuantity;
    }

    public float getCartExtension() {
        return mCartExtension;
    }
    public void setCartExtension(float cartExtension) {
        mCartExtension = cartExtension;
    }
}
