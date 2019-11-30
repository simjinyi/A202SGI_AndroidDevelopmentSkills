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

    public void setProductCartQuantity(int quantity, int position) {
        if (quantity <= getProductList().getValue().get(position).getInventoryQuantity() && quantity >= 0) {
            mProductList.getValue().get(position).setCartQuantity(quantity);
            mProductList.setValue(mProductList.getValue());

            calculateTotalPrice();
            calculateCartQuantity();
        }
    }

    public void calculateTotalPrice() {
        float total = 0.0f;
        for (Product product : mProductList.getValue())
            total += product.getCartQuantity() * product.getPrice();
        mTotalPrice.setValue(total);
    }

    public void calculateCartQuantity() {
        int quantity = 0;
        for (Product product : mProductList.getValue())
            if (product.getCartQuantity() > 0)
                quantity += 1;
        mCartQuantity.setValue(quantity);
    }

    public void resetProductCartQuantity() {
        for (int i = 0; i < mProductList.getValue().size(); i++)
            setProductCartQuantity(0, i);
    }

    public void refresh() {
        ProductRepository.getInstance(mStoreId).getProducts();
    }

    public void insertProduct(Product product, OnSuccessListener onSuccessListener) {
        if (!checkNameExists(product.getName()))
            ProductRepository.getInstance(mStoreId).insert(product, onSuccessListener);
        else
            onSuccessListener.onSuccess(PRODUCT_NAME_DUPLICATE);
    }

    public void updateProduct(Product oriProduct, Product product, OnSuccessListener onSuccessListener) {
        product.setId(oriProduct.getId());
        if (product.getName().equals(oriProduct.getName()) || !checkNameExists(product.getName()))
            ProductRepository.getInstance(mStoreId).update(product, onSuccessListener);
        else
            onSuccessListener.onSuccess(PRODUCT_NAME_DUPLICATE);
    }

    public boolean checkNameExists(String name) {
        for (Product product : mProductList.getValue())
            if (product.getName().equalsIgnoreCase(name))
                return true;
        return false;
    }

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