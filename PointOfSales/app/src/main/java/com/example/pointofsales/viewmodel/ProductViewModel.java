package com.example.pointofsales.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Cart;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.model.sort.ProductSort;
import com.example.pointofsales.model.state.CartOpenableState;
import com.example.pointofsales.model.state.CartRemovalState;
import com.example.pointofsales.model.state.ProductInventoryQuantityChangeState;
import com.example.pointofsales.model.state.ProductLoadState;
import com.example.pointofsales.repository.CartInterface;
import com.example.pointofsales.repository.CartRepository;
import com.example.pointofsales.repository.ProductInterface;
import com.example.pointofsales.repository.ProductRepository;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * ProductViewModel handles product and cart operations
 * Implements ChildEventListener to observe changes on the product data
 * Implements ProductInterface to receive callback on product existence check
 * Implements CartInterface to observe cart changes
 */
public class ProductViewModel extends ViewModel implements ChildEventListener, ProductInterface, CartInterface {

    // Constant to denote product name duplication
    private static final int PRODUCT_NAME_DUPLICATE = 1;

    private String mStoreId;
    private ProductSort mProductSort;

    // MutableLiveData to be observed by the View components
    private MutableLiveData<ProductLoadState> mProductLoadState;
    private MutableLiveData<CartOpenableState> mCartOpenableState;
    private MutableLiveData<ProductInventoryQuantityChangeState> mProductInventoryQuantityChangeState;
    private MutableLiveData<CartRemovalState> mCartRemovalState;

    private MutableLiveData<Boolean> mProductRemoved;
    private MutableLiveData<Boolean> mProductMissing;

    private MutableLiveData<ArrayList<Product>> mProductList;
    private MutableLiveData<ArrayList<Product>> mCartList;

    private MutableLiveData<Cart> mCart;

    // Repositories
    private ProductRepository mProductRepository;
    private CartRepository mCartRepository;

    public ProductViewModel() {

        mStoreId = UserViewModel.getUserId();
        mProductSort = new ProductSort();

        mProductRepository = ProductRepository.getInstance(mStoreId, this, this);
        mCartRepository = CartRepository.getInstance(mStoreId);

        // Instantiate the MutableLiveData
        mProductLoadState = new MutableLiveData<>();
        mProductLoadState.setValue(ProductLoadState.LOADING);
        checkProductExists();

        mCartOpenableState = new MutableLiveData<>();
        mCartOpenableState.setValue(CartOpenableState.DISABLED);
        checkCartExists();

        mProductInventoryQuantityChangeState = new MutableLiveData<>();
        mProductInventoryQuantityChangeState.setValue(new ProductInventoryQuantityChangeState());

        mCartRemovalState = new MutableLiveData<>();
        mCartRemovalState.setValue(new CartRemovalState());

        mProductRemoved = new MutableLiveData<>();
        mProductRemoved.setValue(false);

        mProductMissing = new MutableLiveData<>();
        mProductMissing.setValue(false);

        mProductList = mProductRepository.getProducts();
        mCartList = mProductRepository.getCartItems();

        mCart = mCartRepository.getCart();
    }

    // CART HANDLER

    /**
     * Add cart quantity with validation
     * @param position position to update the cart quantity
     */
    public void addCartQuantity(int position) {
        // If product is missing from the list
        if (position < 0) {
            mProductMissing.setValue(true);
            return;
        }

        int cartQuantityAdded = mProductRepository.get(position).getCartQuantity() + 1;
        int inventoryQuantity = mProductRepository.get(position).getInventoryQuantity();

        // Validate the cart quantity against the inventory quantity
        if (cartQuantityAdded <= inventoryQuantity)
            updateCartItem(cartQuantityAdded, position);
    }

    /**
     * Add quantity by product
     * @param product product to be added with the quantity
     */
    public void addCartQuantity(Product product) {
        // Cascade call to the addCartQuantity(int) by getting the index
        addCartQuantity(mProductRepository.getProductIndexFromProduct(product));
    }

    /**
     * Minus cart quantity
     * @param position position to update the cart quantity
     */
    public void minusCartQuantity(int position) {
        // If product is missing from the list
        if (position < 0) {
            mProductMissing.setValue(true);
            return;
        }

        int cartQuantitySubtracted = mProductRepository.get(position).getCartQuantity() - 1;

        // Validate the cart quantity against 0
        if (cartQuantitySubtracted >= 0)
            updateCartItem(cartQuantitySubtracted, position);
    }

    /**
     * Minus quantity by product
     * @param product product to be subtracted with the quantity
     */
    public void minusCartQuantity(Product product) {
        // Cascade call to the minusCartQuantity(int) by getting the index
        minusCartQuantity(mProductRepository.getProductIndexFromProduct(product));
    }

    /**
     * Reset the cart
     */
    public void resetCart() {
        for (int i = 0; i < mProductRepository.getProducts().getValue().size(); i++)
            updateCartItem(0, i); // Update all the cart quantity to 0
    }

    /**
     * Update the cart (recalculate upon invocation)
     */
    private void updateCart() {

        // Clear the cart list
        mCartList.getValue().clear();
        ArrayList<String> productNames = new ArrayList<>();

        for (int i = 0; i < mProductRepository.getProducts().getValue().size(); i++) {
            Product product = mProductRepository.getProducts().getValue().get(i);

            // If the cart value was changed due to a change in the product inventory quantity
            if (product.getInventoryQuantity() < product.getCartQuantity()) {
                updateCartItem(product.getInventoryQuantity(), i);  // Update the cart quantity as the inventory quantity
                productNames.add(product.getName());                // Notify cart changed
            } else {
                updateCartItem(product.getCartQuantity(), i);
            }
        }

        calculateCartDetails();
        mProductInventoryQuantityChangeState.setValue(new ProductInventoryQuantityChangeState(productNames));
    }

    /**
     * Update the cart item
     * @param quantity
     * @param position
     */
    private void updateCartItem(int quantity, int position) {
        // If the product is missing
        if (position < 0) {
            mProductMissing.setValue(true);
            return;
        }

        // Set the cart quantity
        mProductRepository
                .get(position)
                .setCartQuantity(quantity);

        // Set the cart extension
        mProductRepository
                .get(position)
                .setCartExtension(quantity * mProductRepository.get(position).getPrice());

        ArrayList<Product> allProducts = mProductList.getValue();
        ArrayList<Product> cartProducts = mCartList.getValue();

        // If the cart quantity is greater than 0 but is absent in the cartList, add the product
        if (quantity > 0 && !cartProducts.contains(allProducts.get(position)))
            cartProducts.add(allProducts.get(position));
        else if (quantity <= 0 && cartProducts.contains(allProducts.get(position)))
            // If the cart quantity is less than 0 but is present in the cartList, remove the product
            cartProducts.remove(allProducts.get(position));

        mProductRepository.notifyObservers();
        calculateCartDetails();
    }

    /**
     * Calculate the subtotal, cart quantity and check if cart exists
     */
    private void calculateCartDetails() {
        calculateSubtotalPrice();
        calculateCartQuantity();
        checkCartExists();
        mCartRepository.notifyObservers();
    }

    /**
     * Calculate the subtotal
     */
    private void calculateSubtotalPrice() {
        float total = 0.0f;
        for (Product product : mProductRepository.getProducts().getValue())
            total += product.getCartQuantity() * product.getPrice();
        mCartRepository.getCart().getValue().setSubtotal(total);
    }

    /**
     * Calculate the cart quantity
     */
    private void calculateCartQuantity() {
        int quantity = 0;
        for (Product product : mProductRepository.getProducts().getValue())
            if (product.getCartQuantity() > 0)
                quantity++;
        mCartRepository.getCart().getValue().setCartQuantity(quantity);
    }

    /**
     * Check if the cart contains at least one product
     */
    private void checkCartExists() {
        // Update the cart openable state
        if (mProductRepository.getCartItems().getValue().size() > 0)
            mCartOpenableState.setValue(CartOpenableState.ENABLED);
        else
            mCartOpenableState.setValue(CartOpenableState.DISABLED);
    }

    // Flags clearer
    public void clearProductInventoryQuantityChangeFlag() {
        mProductInventoryQuantityChangeState.setValue(new ProductInventoryQuantityChangeState());
    }

    public void clearCartRemovalFlag() {
        mCartRemovalState.setValue(new CartRemovalState());
    }

    public void clearProductMissingFlag() {
        mProductMissing.setValue(false);
    }
    // END Flags clearer
    // END CART HANDLER

    // PRODUCT HANDLER

    /**
     * Get product index in the ArrayList from the product object
     * @param product product to be checked
     * @return index in the ArrayList
     */
    public int getProductIndexFromProduct(Product product) {
        // Call to the repository to get the product index
        return mProductRepository.getProductIndexFromProduct(product);
    }

    /**
     * Insert a new product into the database
     * @param product product to be inserted
     * @param onSuccessListener callback on successful operation
     */
    public void insertProduct(Product product, OnSuccessListener onSuccessListener) {

        // If the product name is valid
        if (validateProductName(product.getName())) {

            // Insert the product into the database
            product.setStoreId(mStoreId);
            mProductRepository.insert(product, onSuccessListener);
            return;
        }

        // Otherwise prompt error
        onSuccessListener.onSuccess(PRODUCT_NAME_DUPLICATE);
    }

    /**
     * Update product in the database
     * @param oriProduct original product before update
     * @param product product to be updated
     * @param onSuccessListener callback on successful operation
     */
    public void updateProduct(Product oriProduct, Product product, final OnSuccessListener onSuccessListener) {

        // Check if the name is the same, otherwise check if the product name changed was diplicated
        if (product.getName().equals(oriProduct.getName()) || validateProductName(product.getName())) {

            // Update the product into the database if the check was valid
            product.setId(oriProduct.getId());
            product.setStoreId(mStoreId);
            mProductRepository.update(product, onSuccessListener);
            return;
        }

        // Otherwise prompt error
        onSuccessListener.onSuccess(PRODUCT_NAME_DUPLICATE);
    }

    /**
     * Delete product from the database
     * @param product
     */
    public void deleteProduct(Product product) {

        // Delete the product from the repository
        mProductRepository.delete(product, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                // On complete set the product removed flag to true
                mProductRemoved.setValue(true);
            }
        });
    }

    /**
     * Move the product in the list
     * @param fromPosition from the original position
     * @param toPosition to the intended position
     */
    public void moveProduct(int fromPosition, int toPosition) {

        // Call to the product repository to move the product
        mProductRepository.move(fromPosition, toPosition);
    }

    /**
     * Validate the product name (whether if it exists in the database)
     * @param name name of the product
     * @return validity of the name
     */
    private boolean validateProductName(String name) {
        // If the name exists, return false
        for (Product product : mProductRepository.getProducts().getValue())
            if (product.getName().equalsIgnoreCase(name))
                return false;
        return true;
    }

    /**
     * Check if the at least one product exists
     */
    private void checkProductExists() {
        mProductRepository.check(this);
    }

    /**
     * Sort function used in sorting the products in the Product RecyclerView
     * @return the String resource containing the way the products are sorted
     */
    public int sort() {
        switch (mProductSort.next()) {
            case NAME_ASC:
                mProductRepository.sortNameAsc();
                return R.string.nameAscending;
            case NAME_DESC:
                mProductRepository.sortNameDesc();
                return R.string.nameDescending;
            case PRICE_ASC:
                mProductRepository.sortPriceAsc();
                return R.string.priceAscending;
            case PRICE_DESC:
                mProductRepository.sortPriceDesc();
                return R.string.priceDescending;
            case INVENTORY_ASC:
                mProductRepository.sortInventoryAsc();
                return R.string.inventoryAscending;
            case INVENTORY_DESC:
                mProductRepository.sortInventoryDesc();
                return R.string.inventoryDescending;
            case CART_ASC:
                mProductRepository.sortCartAsc();
                return R.string.cartAscending;
            default:
                mProductRepository.sortCartDesc();
                return R.string.cartDescending;
        }
    }

    /**
     * Clear the product removed flag
     */
    public void clearProductRemoved() {
        mProductRemoved.setValue(false);
    }
    // END PRODUCT HANDLER

    // GETTER METHODS
    public LiveData<ArrayList<Product>> getProductList() {
        return mProductList;
    }
    public LiveData<ArrayList<Product>> getCartList() {
        return mCartList;
    }
    public LiveData<Cart> getCart() {
        return mCart;
    }
    public LiveData<ProductLoadState> getProductLoadState() {
        return mProductLoadState;
    }
    public LiveData<CartOpenableState> getCartOpenableState() {
        return mCartOpenableState;
    }
    public LiveData<ProductInventoryQuantityChangeState> getProductInventoryQuantityChangeState() {
        return mProductInventoryQuantityChangeState;
    }
    public LiveData<CartRemovalState> getCartRemovalState() {
        return mCartRemovalState;
    }
    public LiveData<Boolean> getProductRemoved() {
        return mProductRemoved;
    }
    public LiveData<Boolean> getProductMissing() {
        return mProductMissing;
    }
    // END GETTER METHODS

    // Child Event Listeners
    // On any change check if the product exists and update the cart value
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
        // ignore
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        // ignore
    }

    /**
     * Check if at least one product exists
     * @param existence if the product exists
     */
    @Override
    public void productExistCallback(boolean existence) {

        // Update the product load state
        mProductLoadState.setValue(existence ? ProductLoadState.LOADED : ProductLoadState.NO_PRODUCT);
    }

    /**
     * Notify the View observers to update the cart
     * @param productNames product names where the carts were updated
     */
    @Override
    public void notifyCartChanged(ArrayList<String> productNames) {
        mCartRemovalState.setValue(new CartRemovalState(productNames));
    }

    /**
     * Get store id in the ViewModel
     * @return storeId
     */
    public String getStoreId() {
        return mStoreId;
    }

    /**
     * Notify the cart observers that the cart has changed
     */
    public void notifyCartObservers() {
        mCart.setValue(mCart.getValue());
    }
}