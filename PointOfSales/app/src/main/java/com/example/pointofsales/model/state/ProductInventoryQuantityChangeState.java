package com.example.pointofsales.model.state;

import androidx.annotation.NonNull;

import java.util.ArrayList;

/**
 * Model class to store the products when the inventory quantity was updated
 */
public class ProductInventoryQuantityChangeState {

    private ArrayList<String> mProductNames;

    public ProductInventoryQuantityChangeState() {
        mProductNames = new ArrayList<>();
    }

    public ProductInventoryQuantityChangeState(ArrayList<String> productNames) {
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
