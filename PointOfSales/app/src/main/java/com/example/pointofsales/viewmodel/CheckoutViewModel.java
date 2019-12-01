package com.example.pointofsales.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.model.Cart;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.repository.CartRepository;
import com.example.pointofsales.repository.ProductRepository;

import java.util.ArrayList;

public class CheckoutViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Cart>> mCartItems;

    private MutableLiveData<Float> mSubtotalPrice;
    private MutableLiveData<Float> mTotalPrice;
    private MutableLiveData<Integer> mCartQuantity;
    private String mStoreId;

    public CheckoutViewModel() {
        mStoreId = "0";
        mCartItems = CartRepository.getInstance().getCartItems();

        mSubtotalPrice = new MutableLiveData<>();
        mSubtotalPrice.setValue(0.0f);

        mTotalPrice = new MutableLiveData<>();
        mTotalPrice.setValue(0.0f);

        mCartQuantity = new MutableLiveData<>();
        mCartQuantity.setValue(0);
    }

    // CART HANDLER
    public void addProductToCart(Product product) {
        CartRepository.getInstance()
                .insert(new Cart(product));
    }

    public void deleteProductFromCart(int position) {
        CartRepository.getInstance()
                .delete(position);
    }

    public int getCartQuantity(int position) {
        return CartRepository.getInstance().get(position).getCartQuantity();
    }

    public int getCartQuantity(Product product) {
        int index = CartRepository.getInstance().getCartIndexFromProduct(product);
        if (index == -1)
            return 0;
        return getCartQuantity(index);
    }

    public void addCartQuantity(int position) {
        int cartQuantityAdded = CartRepository.getInstance()
                .get(position)
                .getCartQuantity() + 1;
        int inventoryQuantity = CartRepository.getInstance()
                .get(position)
                .getProduct()
                .getInventoryQuantity();

        if (cartQuantityAdded <= inventoryQuantity) {
            updateCartItemQuantityAndExtension(cartQuantityAdded, position);
            CartRepository.getInstance().notifyObservers();
        }
    }

    public void addCartQuantity(Product product) {
        int index = CartRepository.getInstance().getCartIndexFromProduct(product);
        if (index == -1) {
            addProductToCart(product);
            index = CartRepository.getInstance().getCartItems().getValue().size() - 1;
        }
        addCartQuantity(index);
    }

    public void minusCartQuantity(int position) {
        int cartQuantitySubtracted = CartRepository.getInstance()
                .get(position)
                .getCartQuantity() - 1;

        if (cartQuantitySubtracted >= 0) {
            updateCartItemQuantityAndExtension(cartQuantitySubtracted, position);

            if (cartQuantitySubtracted == 0)
                deleteProductFromCart(position);

            CartRepository.getInstance().notifyObservers();
        }
    }

    public void minusCartQuantity(Product product) {
        int index = CartRepository.getInstance().getCartIndexFromProduct(product);
        if (index == -1)
            return;
        minusCartQuantity(index);
    }

    public void resetCartItems() {
        for (int i = 0; i < CartRepository.getInstance().getCartItems().getValue().size(); i++)
            updateCartItemQuantityAndExtension(0, i);
    }

    private void updateCartItemQuantityAndExtension(int quantity, int position) {
        CartRepository.getInstance()
                .get(position)
                .setCartQuantity(quantity);

        CartRepository.getInstance()
                .get(position)
                .setCartExtension(quantity * CartRepository.getInstance().get(position).getProduct().getPrice());

        CartRepository.getInstance()
                .notifyObservers();

        calculateCartSubtotalPrice();
        calculateCartQuantity();
    }

    private void calculateCartSubtotalPrice() {
        float total = 0.0f;
        for (Cart cart : CartRepository.getInstance().getCartItems().getValue())
            total += cart.getCartQuantity() * cart.getProduct().getPrice();
        mSubtotalPrice.setValue(total);
    }

    private void calculateCartQuantity() {
        int quantity = 0;
        for (Cart cart : CartRepository.getInstance().getCartItems().getValue())
            if (cart.getCartQuantity() > 0)
                quantity += 1;
        mCartQuantity.setValue(quantity);
    }

    public boolean isProductInCart(Product product) {
        return CartRepository.getInstance().getCartIndexFromProduct(product) != -1;
    }
    // END CART HANDLER

    public LiveData<ArrayList<Cart>> getCartItems() {
        return mCartItems;
    }
    public LiveData<Float> getSubtotalPrice() { return mSubtotalPrice; }
    public LiveData<Float> getTotalPrice() {
        return mTotalPrice;
    }
    public LiveData<Integer> getCartQuantity() {
        return mCartQuantity;
    }
}
