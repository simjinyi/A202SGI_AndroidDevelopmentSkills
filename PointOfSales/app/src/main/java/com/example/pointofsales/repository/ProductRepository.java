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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
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
    public void check(final ProductInterface productInterface) {
        ProductDatabase.getInstance(mStoreId)
                .check(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        productInterface.checkIfProductExists(dataSnapshot.exists());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        productInterface.checkIfProductExists(false);
                    }
                });
    }

    public Product get(int index) {
        return mProducts.getValue().get(index);
    }

    public void insert(Product product, OnSuccessListener onSuccessListener) {
        ProductDatabase.getInstance(mStoreId).insert(ProductDatabase.Converter.productToMap(product), onSuccessListener);
    }

    public void update(Product product, OnSuccessListener onSuccessListener) {
        ProductDatabase.getInstance(mStoreId).update(ProductDatabase.Converter.productToMap(product), onSuccessListener);
    }

    public void move(int fromIndex, int toIndex) {
        if (fromIndex < toIndex)
            for (int i = fromIndex; i < toIndex; i++)
                Collections.swap(mProducts.getValue(), i, i + 1);
        else
            for (int i = fromIndex; i > toIndex; i--)
                Collections.swap(mProducts.getValue(), i, i - 1);
        notifyObservers();
    }
    // END OPERATIONS

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        if (dataSnapshot.exists()) {
            mProducts.getValue().add(ProductDatabase.Converter.mapToProduct(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue()));
            mChildEventListener.onChildAdded(dataSnapshot, s);
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
        mChildEventListener.onChildChanged(dataSnapshot, s);
        notifyObservers();
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
        Product removedProduct = ProductDatabase.Converter.mapToProduct(dataSnapshot.getKey(), data);
        mProducts.getValue()
                .remove(getProductIndexFromProductId(removedProduct.getId()));

        int cartPosition = 0;
        if ((cartPosition = getCartIndexFromProductId(removedProduct.getId())) != -1)
            mCartItems.getValue()
                    .remove(cartPosition);

        mChildEventListener.onChildRemoved(dataSnapshot);
        notifyObservers();
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        mChildEventListener.onChildMoved(dataSnapshot, s);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        mChildEventListener.onCancelled(databaseError);
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
