package com.example.pointofsales.controller;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.model.Product;
import com.example.pointofsales.model.ProductList;
import com.example.pointofsales.repository.ProductRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class ProductViewModel extends ViewModel implements ValueEventListener {

    private MutableLiveData<ProductList> mProductList;
    private MutableLiveData<Float> mTotalPrice;
    private MutableLiveData<Integer> mCartQuantity;

    public ProductViewModel() {
        mProductList = new MutableLiveData<>();
        mProductList.setValue(new ProductList());

        mTotalPrice = new MutableLiveData<>();
        mTotalPrice.setValue(0.0f);

        mCartQuantity = new MutableLiveData<>();
        mCartQuantity.setValue(0);
    }

    public void setProductCartQuantity(int quantity, int position) {
        mProductList.getValue().getProductByIndex(position).setCartQuantity(quantity);
        mProductList.setValue(mProductList.getValue());

        calculateTotalPrice();
        calculateCartQuantity();
    }

    public void calculateTotalPrice() {
        float total = 0.0f;

        for (Product product : mProductList.getValue().getProducts())
            total += product.getCartQuantity() * product.getPrice();

        mTotalPrice.setValue(total);
    }

    public void calculateCartQuantity() {
        int quantity = 0;

        for (Product product : mProductList.getValue().getProducts())
            if (product.getCartQuantity() > 0)
                quantity += 1;

        mCartQuantity.setValue(quantity);
    }

    public void resetProductCartQuantity() {
        for (int i = 0; i < mProductList.getValue().getProductListSize(); i++)
            setProductCartQuantity(0, i);
    }

    public void refresh() {
        ProductRepository.getInstance().addValueEventListener("0", this);
    }

    public LiveData<ProductList> getProducts() {
        if (mProductList.getValue().getProductListSize() == 0)
            refresh();
        return mProductList;
    }

    public LiveData<Float> getTotalPrice() {
        return mTotalPrice;
    }

    public LiveData<Integer> getCartQuantity() {
        return mCartQuantity;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        ArrayList<Product> productsArrayList = new ArrayList<>();

        if (dataSnapshot.exists())
            for (DataSnapshot child : dataSnapshot.getChildren())
                productsArrayList.add(ProductRepository.mapToProduct(child.getKey(), (Map<String, Object>) child.getValue()));

        mProductList.getValue().setProducts(productsArrayList);
        mProductList.setValue(mProductList.getValue());
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        mProductList.setValue(mProductList.getValue());
    }
}