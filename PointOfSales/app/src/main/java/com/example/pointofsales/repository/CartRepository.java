package com.example.pointofsales.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.pointofsales.model.Cart;

/**
 * CartRepository Singleton to provide an interface between the ViewModels and the Model
 */
public class CartRepository {

    private String mStoreId;
    private MutableLiveData<Cart> mCart; // MutableLiveData so that the view can be dynamically updated when the cart details was updated

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

    public static void clearInstance() {
        sCartRepository = null;
    }
}
