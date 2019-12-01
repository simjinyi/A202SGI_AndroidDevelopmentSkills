package com.example.pointofsales.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.pointofsales.model.Cart;

public class CartRepository {

    private String mStoreId;
    private MutableLiveData<Cart> mCart;

    private static CartRepository sCartRepository;

    private CartRepository(String storeId) {
        mStoreId = storeId;
        mCart = new MutableLiveData<>();
        mCart.setValue(new Cart());
    }

    public static CartRepository getInstance(String storeId) {
        if (sCartRepository == null)
            sCartRepository = new CartRepository(storeId);
        return sCartRepository;
    }

    public void notifyObservers() {
        mCart.setValue(mCart.getValue());
    }

    public MutableLiveData<Cart> getCart() {
        return mCart;
    }
}
