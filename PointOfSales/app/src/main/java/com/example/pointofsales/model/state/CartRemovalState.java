package com.example.pointofsales.model.state;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * Model class to store the removed items in the cart
 */
public class CartRemovalState {

    private ArrayList<String> mProductNames;

    public CartRemovalState() {
        mProductNames = new ArrayList<>();
    }

    public CartRemovalState(ArrayList<String> productNames) {
        mProductNames = productNames;
    }

    public ArrayList<String> getProductNames() {
        return mProductNames;
    }

    public void setProductNames(ArrayList<String> productNames) {
        mProductNames = productNames;
    }

    @NonNull
    @Override
    public String toString() {
        if (mProductNames.size() <= 0)
            return "";

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < mProductNames.size(); i++) {
            stringBuilder.append(mProductNames.get(i));
            stringBuilder.append((i == mProductNames.size() - 1) ? "" : ", ");
        }

        return stringBuilder.toString();
    }
}
