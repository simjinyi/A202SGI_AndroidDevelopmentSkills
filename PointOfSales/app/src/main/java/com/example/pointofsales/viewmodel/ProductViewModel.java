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

    private MutableLiveData<ArrayList<Product>> mProductList;

    private MutableLiveData<Float> mTotalPrice;
    private MutableLiveData<Integer> mCartQuantity;

    private String mStoreId;

    public ProductViewModel() {
        mStoreId = "0";

        mProductList = ProductRepository.getInstance(mStoreId).getProducts();

        mTotalPrice = new MutableLiveData<>();
        mTotalPrice.setValue(0.0f);

        mCartQuantity = new MutableLiveData<>();
        mCartQuantity.setValue(0);
    }

    // CART HANDLER
    public void addProductCartQuantity(int position) {
        int quantity = getProductList().getValue().get(position).getCartQuantity();
        if (quantity + 1 <= getProductList().getValue().get(position).getInventoryQuantity())
            updateProductCartQuantityAndExtension(quantity + 1, position);
    }

    public void minusProductCartQuantity(int position) {
        int quantity = getProductList().getValue().get(position).getCartQuantity();
        if (quantity - 1 >= 0)
            updateProductCartQuantityAndExtension(quantity - 1, position);
    }

    public void resetProductCartQuantity() {
        for (int i = 0; i < mProductList.getValue().size(); i++)
            updateProductCartQuantityAndExtension(0, i);
    }

    private void updateProductCartQuantityAndExtension(int quantity, int position) {
        ProductRepository.getInstance(mStoreId).updateCartQuantityAndExtension(quantity, quantity * mProductList.getValue().get(position).getPrice(), position);
        calculateTotalPrice();
        calculateCartQuantity();
    }

    private void calculateTotalPrice() {
        float total = 0.0f;
        for (Product product : mProductList.getValue())
            total += product.getCartQuantity() * product.getPrice();
        mTotalPrice.setValue(total);
    }

    private void calculateCartQuantity() {
        int quantity = 0;
        for (Product product : mProductList.getValue())
            if (product.getCartQuantity() > 0)
                quantity += 1;
        mCartQuantity.setValue(quantity);
    }
    // END CART HANDLER

    // PRODUCT HANDLER
    public void insertProduct(Product product, OnSuccessListener onSuccessListener) {
        if (validateProductName(product.getName()))
            ProductRepository.getInstance(mStoreId).insert(product, onSuccessListener);
        else
            onSuccessListener.onSuccess(PRODUCT_NAME_DUPLICATE);
    }

    public void updateProduct(Product oriProduct, Product product, OnSuccessListener onSuccessListener) {
        product.setId(oriProduct.getId());
        if (product.getName().equals(oriProduct.getName()) || validateProductName(product.getName()))
            ProductRepository.getInstance(mStoreId).update(product, onSuccessListener);
        else
            onSuccessListener.onSuccess(PRODUCT_NAME_DUPLICATE);
    }

    private boolean validateProductName(String name) {
        for (Product product : mProductList.getValue())
            if (product.getName().equalsIgnoreCase(name))
                return false;
        return true;
    }
    // END PRODUCT HANDLER

    public LiveData<ArrayList<Product>> getProductList() {
        return mProductList;
    }
    public LiveData<Float> getTotalPrice() {
        return mTotalPrice;
    }
    public LiveData<Integer> getCartQuantity() {
        return mCartQuantity;
    }
}