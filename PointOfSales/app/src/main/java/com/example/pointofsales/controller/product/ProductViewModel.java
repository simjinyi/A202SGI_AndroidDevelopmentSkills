package com.example.pointofsales.controller.product;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.model.Product;
import com.example.pointofsales.repository.ProductRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class ProductViewModel extends ViewModel implements ValueEventListener {

    private MutableLiveData<ArrayList<Product>> mProducts;
    private MutableLiveData<Float> mTotalPrice;
    private MutableLiveData<Integer> mCartQuantity;

    public ProductViewModel() {
        mProducts = new MutableLiveData<>();
        mProducts.setValue(new ArrayList<Product>());

        mTotalPrice = new MutableLiveData<>();
        mTotalPrice.setValue(0.0f);

        mCartQuantity = new MutableLiveData<>();
        mCartQuantity.setValue(0);
    }

    public void setProductCartQuantity(int quantity, int position) {
        mProducts.getValue().get(position).setCartQuantity(quantity);
        mProducts.setValue(mProducts.getValue());

        calculateTotalPrice();
        calculateCartQuantity();
    }

    public void calculateTotalPrice() {
        float total = 0.0f;

        for (Product product : mProducts.getValue())
            total += product.getCartQuantity() * product.getPrice();

        mTotalPrice.setValue(total);
    }

    public void calculateCartQuantity() {
        int quantity = 0;

        for (Product product : mProducts.getValue())
            if (product.getCartQuantity() > 0)
                quantity += 1;

        mCartQuantity.setValue(quantity);
    }

    public void resetProductCartQuantity() {
        for (int i = 0; i < mProducts.getValue().size(); i++)
            setProductCartQuantity(0, i);
    }

    public void refresh() {
        ProductRepository.getInstance().addValueEventListener("0", this);
    }

    public LiveData<ArrayList<Product>> getProducts() {
        if (mProducts.getValue().size() == 0)
            refresh();
        return mProducts;
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
                productsArrayList.add(ProductRepository.mapToProduct((Map<String, Object>) child.getValue()));

        mProducts.setValue(productsArrayList);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        mProducts.setValue(mProducts.getValue());
    }
}