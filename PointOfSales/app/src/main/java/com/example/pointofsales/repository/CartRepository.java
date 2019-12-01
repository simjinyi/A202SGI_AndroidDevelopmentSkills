package com.example.pointofsales.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.pointofsales.model.Cart;
import com.example.pointofsales.model.Product;

import java.util.ArrayList;

public class CartRepository {

    private MutableLiveData<ArrayList<Cart>> mCartItems;

    private static CartRepository sCartRepository;

    private CartRepository() {
        mCartItems = new MutableLiveData<>();
        mCartItems.postValue(new ArrayList<Cart>());
    }

    public static CartRepository getInstance() {
        if (sCartRepository == null)
            sCartRepository = new CartRepository();
        return sCartRepository;
    }

    // OPERATIONS
    public Cart get(int index) {
        return mCartItems.getValue().get(index);
    }

    public void insert(Cart cart) {
        mCartItems.getValue().add(cart);
        notifyObservers();
    }

    public void delete(int index) {
        mCartItems.getValue().remove(index);
        notifyObservers();
    }
    // END OPERATIONS

    public int getCartIndexFromProduct(Product product) {
        for (int i = 0; i < mCartItems.getValue().size(); i++)
            if (mCartItems.getValue().get(i).getProduct() == product)
                return i;
        return -1;
    }

    public void notifyObservers() {
        mCartItems.setValue(mCartItems.getValue());
    }

    public MutableLiveData<ArrayList<Cart>> getCartItems() {
        return mCartItems;
    }
}
