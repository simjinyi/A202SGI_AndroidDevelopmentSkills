package com.example.pointofsales.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.pointofsales.database.ProductDatabase;
import com.example.pointofsales.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Map;

public class ProductRepository implements ChildEventListener {

    private String mStoreId;
    private MutableLiveData<ArrayList<Product>> mProducts;
    private MutableLiveData<ArrayList<Product>> mCartItems;
    private ChildEventListener mChildEventListener;

    private static ProductRepository sProductRepository;

    private ProductRepository(String storeId, ChildEventListener childEventListener) {
        mStoreId = storeId;
        mProducts = new MutableLiveData<>();
        mProducts.setValue(new ArrayList<Product>());
        mCartItems = new MutableLiveData<>();
        mCartItems.setValue(new ArrayList<Product>());
        ProductDatabase.getInstance(mStoreId).get(this);
        mChildEventListener = childEventListener;
    }

    public static ProductRepository getInstance(String storeId, ChildEventListener childEventListener) {
        if (sProductRepository == null)
            sProductRepository = new ProductRepository(storeId, childEventListener);
        return sProductRepository;
    }

    // OPERATIONS
    public Product get(int index) {
        return mProducts.getValue().get(index);
    }

    public void insert(Product product, OnSuccessListener onSuccessListener) {
        ProductDatabase.getInstance(mStoreId).insert(ProductDatabase.Converter.productToMap(product), onSuccessListener);
    }

    public void update(Product product, OnSuccessListener onSuccessListener) {
        ProductDatabase.getInstance(mStoreId).update(ProductDatabase.Converter.productToMap(product), onSuccessListener);
    }
    // END OPERATIONS

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        if (dataSnapshot.exists()) {
            mProducts.getValue().add(ProductDatabase.Converter.mapToProduct(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue()));
            notifyObservers();
        }
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Product changedProduct = ProductDatabase.Converter.mapToProduct(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue());
        int changedProductIndex = getProductIndexFromProductId(changedProduct.getId());
        Product originalProduct = mProducts.getValue()
                .get(changedProductIndex);

        changedProduct.setCartQuantity(originalProduct.getCartQuantity());
        changedProduct.setCartExtension(originalProduct.getCartExtension());
        mProducts.getValue()
                .set(changedProductIndex, changedProduct);
        notifyObservers();
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        Product removedProduct = ProductDatabase.Converter.mapToProduct(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue());
        mProducts.getValue()
                .remove(getProductIndexFromProductId(removedProduct.getId()));
        mCartItems.getValue()
                .remove(getCartIndexFromProductId(removedProduct.getId()));
        mChildEventListener.onChildRemoved(dataSnapshot);
        notifyObservers();
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    private int getCartIndexFromProductId(String productId) {
        for (int i = 0; i < mCartItems.getValue().size(); i++)
            if (mCartItems.getValue().get(i).getId().equals(productId))
                return i;
        return -1;
    }

    private int getProductIndexFromProductId(String productId) {
        for (int i = 0; i < mProducts.getValue().size(); i++)
            if (mProducts.getValue().get(i).getId().equals(productId))
                return i;
        return -1;
    }

    public int getProductIndexFromProduct(Product product) {
        for (int i = 0; i < mProducts.getValue().size(); i++)
            if (product == mProducts.getValue().get(i))
                return i;
        return -1;
    }

    public void notifyObservers() {
        mProducts.setValue(mProducts.getValue());
        mCartItems.setValue(mCartItems.getValue());
    }

    public MutableLiveData<ArrayList<Product>> getProducts() {
        return mProducts;
    }
    public MutableLiveData<ArrayList<Product>> getCartItems() {
        return mCartItems;
    }
}
