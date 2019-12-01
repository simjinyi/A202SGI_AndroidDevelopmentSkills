package com.example.pointofsales.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.model.Product;
import com.example.pointofsales.repository.ProductRepository;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class ProductViewModel extends ViewModel {

    public static final int PRODUCT_NAME_DUPLICATE = 1;

    private MutableLiveData<ArrayList<Product>> mProductItems;
    private String mStoreId;

    public ProductViewModel() {
        mStoreId = "0";
        mProductItems = ProductRepository.getInstance(mStoreId).getProducts();
    }

    // PRODUCT HANDLER
    public void insertProduct(Product product, OnSuccessListener onSuccessListener) {
        if (validateProductName(product.getName())) {
            ProductRepository.getInstance(mStoreId).insert(product, onSuccessListener);
            return;
        }

        onSuccessListener.onSuccess(PRODUCT_NAME_DUPLICATE);
    }

    public void updateProduct(Product oriProduct, Product product, OnSuccessListener onSuccessListener) {
        product.setId(oriProduct.getId());

        if (product.getName().equals(oriProduct.getName()) || validateProductName(product.getName())) {
            ProductRepository.getInstance(mStoreId).update(product, onSuccessListener);
            return;
        }

        onSuccessListener.onSuccess(PRODUCT_NAME_DUPLICATE);
    }

    private boolean validateProductName(String name) {
        for (Product product : ProductRepository.getInstance(mStoreId).getProducts().getValue())
            if (product.getName().equalsIgnoreCase(name))
                return false;
        return true;
    }
    // END PRODUCT HANDLER

    public LiveData<ArrayList<Product>> getProductList() {
        return mProductItems;
    }
}