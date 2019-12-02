package com.example.pointofsales.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.model.Cart;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.model.validation.ProductFormState;
import com.example.pointofsales.repository.CartRepository;
import com.example.pointofsales.repository.ProductRepository;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class ProductViewModel extends ViewModel implements ChildEventListener {

    private static final int PRODUCT_NAME_DUPLICATE = 1;

    private String mStoreId;

    private MutableLiveData<ArrayList<Product>> mProductList;
    private MutableLiveData<ArrayList<Product>> mCartList;

    private MutableLiveData<Cart> mCart;

    public ProductViewModel(String storeId) {

        mStoreId = storeId;

        mProductList = ProductRepository.getInstance(mStoreId, this).getProducts();
        mCartList = ProductRepository.getInstance(mStoreId, this).getCartItems();

        mCart = CartRepository.getInstance(mStoreId).getCart();
    }

    // CART HANDLER
    public void addCartQuantity(int position) {
        int cartQuantityAdded = ProductRepository.getInstance(mStoreId, this)
                .get(position)
                .getCartQuantity() + 1;

        int inventoryQuantity = ProductRepository.getInstance(mStoreId, this)
                .get(position)
                .getInventoryQuantity();

        if (cartQuantityAdded <= inventoryQuantity)
            updateCartItem(cartQuantityAdded, position);
    }

    public void addCartQuantity(Product product) {
        addCartQuantity(ProductRepository.getInstance(mStoreId, this).getProductIndexFromProduct(product));
    }

    public void minusCartQuantity(int position) {
        int cartQuantitySubtracted = ProductRepository.getInstance(mStoreId, this)
                .get(position)
                .getCartQuantity() - 1;

        if (cartQuantitySubtracted >= 0)
            updateCartItem(cartQuantitySubtracted, position);
    }

    public void minusCartQuantity(Product product) {
        minusCartQuantity(ProductRepository.getInstance(mStoreId, this).getProductIndexFromProduct(product));
    }

    public void resetCart() {
        for (int i = 0; i < ProductRepository.getInstance(mStoreId, this)
                .getProducts()
                .getValue()
                .size(); i++)
            updateCartItem(0, i);
    }

    private void updateCart() {
        mCartList.getValue().clear();
        for (int i = 0; i < ProductRepository.getInstance(mStoreId, this).getProducts().getValue().size(); i++)
            updateCartItem(ProductRepository.getInstance(mStoreId, this).getProducts().getValue().get(i).getCartQuantity(), i);
        calculateCartDetails();
    }

    private void updateCartItem(int quantity, int position) {
        ProductRepository.getInstance(mStoreId, this)
                .get(position)
                .setCartQuantity(quantity);

        ProductRepository.getInstance(mStoreId, this)
                .get(position)
                .setCartExtension(quantity * ProductRepository.getInstance(mStoreId, this).get(position).getPrice());

        ArrayList<Product> allProducts = mProductList.getValue();
        ArrayList<Product> cartProducts = mCartList.getValue();

        if (quantity > 0 && !cartProducts.contains(allProducts.get(position)))
            cartProducts.add(allProducts.get(position));
        else if (quantity <= 0 && cartProducts.contains(allProducts.get(position)))
            cartProducts.remove(allProducts.get(position));

        ProductRepository.getInstance(mStoreId, this)
                .notifyObservers();

        calculateCartDetails();
    }

    public void calculateCartDetails() {
        calculateSubtotalPrice();
        calculateCartQuantity();
        CartRepository.getInstance(mStoreId)
                .notifyObservers();
    }

    private void calculateSubtotalPrice() {
        float total = 0.0f;
        for (Product product : ProductRepository.getInstance(mStoreId, this).getProducts().getValue())
            total += product.getCartQuantity() * product.getPrice();

        CartRepository.getInstance(mStoreId)
                .getCart()
                .getValue()
                .setSubtotal(total);
    }

    private void calculateCartQuantity() {
        int quantity = 0;
        for (Product product : ProductRepository.getInstance(mStoreId, this).getProducts().getValue())
            if (product.getCartQuantity() > 0)
                quantity++;

        CartRepository.getInstance(mStoreId)
                .getCart()
                .getValue()
                .setCartQuantity(quantity);
    }
    // END CART HANDLER

    // PRODUCT HANDLER
    public void insertProduct(Product product, OnSuccessListener onSuccessListener) {
        if (validateProductName(product.getName())) {
            product.setStoreId(mStoreId);
            ProductRepository.getInstance(mStoreId, this).insert(product, onSuccessListener);
            return;
        }

        onSuccessListener.onSuccess(PRODUCT_NAME_DUPLICATE);
    }

    public void updateProduct(Product oriProduct, Product product, final OnSuccessListener onSuccessListener) {
        if (product.getName().equals(oriProduct.getName()) || validateProductName(product.getName())) {
            product.setId(oriProduct.getId());
            product.setStoreId(mStoreId);
            ProductRepository.getInstance(mStoreId, this).update(product, onSuccessListener);
            return;
        }

        onSuccessListener.onSuccess(PRODUCT_NAME_DUPLICATE);
    }

    private boolean validateProductName(String name) {
        for (Product product : ProductRepository.getInstance(mStoreId, this).getProducts().getValue())
            if (product.getName().equalsIgnoreCase(name))
                return false;
        return true;
    }
    // END PRODUCT HANDLER

    public LiveData<ArrayList<Product>> getProductList() {
        return mProductList;
    }
    public LiveData<ArrayList<Product>> getCartList() { return mCartList; }
    public LiveData<Cart> getCart() {
        return mCart;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        updateCart();
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        updateCart();
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}