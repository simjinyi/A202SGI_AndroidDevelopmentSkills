package com.example.pointofsales.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.model.Cart;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.model.validation.CartOpenableState;
import com.example.pointofsales.model.validation.ProductLoadState;
import com.example.pointofsales.repository.CartRepository;
import com.example.pointofsales.repository.ProductInterface;
import com.example.pointofsales.repository.ProductRepository;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class ProductViewModel extends ViewModel implements ChildEventListener, ProductInterface {

    private static final int PRODUCT_NAME_DUPLICATE = 1;

    private String mStoreId;

    private MutableLiveData<ProductLoadState> mProductLoadState;
    private MutableLiveData<CartOpenableState> mCartOpenableState;
    
    private MutableLiveData<ArrayList<Product>> mProductList;
    private MutableLiveData<ArrayList<Product>> mCartList;

    private MutableLiveData<Cart> mCart;

    private ProductRepository mProductRepository;
    private CartRepository mCartRepository;

    public ProductViewModel(String storeId) {

        mStoreId = storeId;

        mProductRepository = ProductRepository.getInstance(mStoreId, this);
        mCartRepository = CartRepository.getInstance(mStoreId);

        mProductLoadState = new MutableLiveData<>();
        mProductLoadState.setValue(ProductLoadState.LOADING);
        checkProductExists();

        mCartOpenableState = new MutableLiveData<>();
        mCartOpenableState.setValue(CartOpenableState.DISABLED);
        checkCartExists();

        mProductList = mProductRepository.getProducts();
        mCartList = mProductRepository.getCartItems();

        mCart = mCartRepository.getCart();
    }

    // CART HANDLER
    public void addCartQuantity(int position) {
        int cartQuantityAdded = mProductRepository.get(position).getCartQuantity() + 1;
        int inventoryQuantity = mProductRepository.get(position).getInventoryQuantity();

        if (cartQuantityAdded <= inventoryQuantity)
            updateCartItem(cartQuantityAdded, position);
    }

    public void addCartQuantity(Product product) {
        addCartQuantity(mProductRepository.getProductIndexFromProduct(product));
    }

    public void minusCartQuantity(int position) {
        int cartQuantitySubtracted = mProductRepository.get(position).getCartQuantity() - 1;

        if (cartQuantitySubtracted >= 0)
            updateCartItem(cartQuantitySubtracted, position);
    }

    public void minusCartQuantity(Product product) {
        minusCartQuantity(mProductRepository.getProductIndexFromProduct(product));
    }

    public void resetCart() {
        for (int i = 0; i < mProductRepository.getProducts().getValue().size(); i++)
            updateCartItem(0, i);
    }

    private void updateCart() {
        mCartList.getValue().clear();
        for (int i = 0; i < mProductRepository.getProducts().getValue().size(); i++)
            updateCartItem(mProductRepository.getProducts().getValue().get(i).getCartQuantity(), i);
        calculateCartDetails();
    }

    private void updateCartItem(int quantity, int position) {
        mProductRepository
                .get(position)
                .setCartQuantity(quantity);

        mProductRepository
                .get(position)
                .setCartExtension(quantity * mProductRepository.get(position).getPrice());

        ArrayList<Product> allProducts = mProductList.getValue();
        ArrayList<Product> cartProducts = mCartList.getValue();

        if (quantity > 0 && !cartProducts.contains(allProducts.get(position)))
            cartProducts.add(allProducts.get(position));
        else if (quantity <= 0 && cartProducts.contains(allProducts.get(position)))
            cartProducts.remove(allProducts.get(position));

        mProductRepository.notifyObservers();
        calculateCartDetails();
    }

    private void calculateCartDetails() {
        calculateSubtotalPrice();
        calculateCartQuantity();
        checkCartExists();
        mCartRepository.notifyObservers();
    }

    private void calculateSubtotalPrice() {
        float total = 0.0f;
        for (Product product : mProductRepository.getProducts().getValue())
            total += product.getCartQuantity() * product.getPrice();
        mCartRepository.getCart().getValue().setSubtotal(total);
    }

    private void calculateCartQuantity() {
        int quantity = 0;
        for (Product product : mProductRepository.getProducts().getValue())
            if (product.getCartQuantity() > 0)
                quantity++;
        mCartRepository.getCart().getValue().setCartQuantity(quantity);
    }

    private void checkCartExists() {
        if (mProductRepository.getCartItems().getValue().size() > 0)
            mCartOpenableState.setValue(CartOpenableState.ENABLED);
        else
            mCartOpenableState.setValue(CartOpenableState.DISABLED);
    }
    // END CART HANDLER

    // PRODUCT HANDLER
    public void insertProduct(Product product, OnSuccessListener onSuccessListener) {
        if (validateProductName(product.getName())) {
            product.setStoreId(mStoreId);
            mProductRepository.insert(product, onSuccessListener);
            return;
        }

        onSuccessListener.onSuccess(PRODUCT_NAME_DUPLICATE);
    }

    public void updateProduct(Product oriProduct, Product product, final OnSuccessListener onSuccessListener) {
        if (product.getName().equals(oriProduct.getName()) || validateProductName(product.getName())) {
            product.setId(oriProduct.getId());
            product.setStoreId(mStoreId);
            mProductRepository.update(product, onSuccessListener);
            return;
        }

        onSuccessListener.onSuccess(PRODUCT_NAME_DUPLICATE);
    }

    public void moveProduct(int fromPosition, int toPosition) {
        mProductRepository.move(fromPosition, toPosition);
    }

    private boolean validateProductName(String name) {
        for (Product product : mProductRepository.getProducts().getValue())
            if (product.getName().equalsIgnoreCase(name))
                return false;
        return true;
    }

    private void checkProductExists() {
        mProductRepository.check(this);
    }
    // END PRODUCT HANDLER

    public LiveData<ArrayList<Product>> getProductList() {
        return mProductList;
    }
    public LiveData<ArrayList<Product>> getCartList() { return mCartList; }
    public LiveData<Cart> getCart() {
        return mCart;
    }
    public LiveData<ProductLoadState> getProductLoadState() {
        return mProductLoadState;
    }
    public LiveData<CartOpenableState> getCartOpenableState() {
        return mCartOpenableState;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        checkProductExists();
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        updateCart();
        checkProductExists();
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        updateCart();
        checkProductExists();
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    @Override
    public void checkIfProductExists(boolean existence) {
        mProductLoadState.setValue(existence ? ProductLoadState.LOADED : ProductLoadState.NO_PRODUCT);
    }
}