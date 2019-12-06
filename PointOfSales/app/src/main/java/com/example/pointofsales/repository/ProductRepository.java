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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * ProductRepository is a Singleton class used as an intermediary between the Database and the ViewModelss
 */
public class ProductRepository implements ChildEventListener {

    private String mStoreId;
    private MutableLiveData<ArrayList<Product>> mProducts;
    private MutableLiveData<ArrayList<Product>> mCartItems;
    private ChildEventListener mChildEventListener;
    private CartInterface mCartInterface;

    private static ProductRepository sProductRepository;

    private ProductRepository(String storeId, ChildEventListener childEventListener, CartInterface cartInterface) {
        mStoreId = storeId;
        mProducts = new MutableLiveData<>();
        mProducts.setValue(new ArrayList<Product>());
        mCartItems = new MutableLiveData<>();
        mCartItems.setValue(new ArrayList<Product>());
        ProductDatabase.getInstance(mStoreId).get(this);
        mChildEventListener = childEventListener;
        mCartInterface = cartInterface;
    }

    public static ProductRepository getInstance(String storeId, ChildEventListener childEventListener, CartInterface cartInterface) {
        if (sProductRepository == null)
            sProductRepository = new ProductRepository(storeId, childEventListener, cartInterface);
        return sProductRepository;
    }

    // OPERATIONS

    /**
     * Check if the product exists for the logged in seller
     * @param productInterface callback interface to determine if the product exists
     */
    public void check(final ProductInterface productInterface) {
        ProductDatabase.getInstance(mStoreId)
                .check(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        productInterface.productExistCallback(dataSnapshot.exists());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        productInterface.productExistCallback(false);
                    }
                });
    }

    /**
     * Get the product from the local storage
     * @param index index of the product in the ArrayList
     * @return product
     */
    public Product get(int index) {
        return mProducts.getValue().get(index);
    }

    /**
     * Intermediary between the ViewModels and the Database to insert the product
     * @param product product object to be inserted
     * @param onSuccessListener callback listener when the process was succeeded
     */
    public void insert(Product product, OnSuccessListener onSuccessListener) {
        ProductDatabase.getInstance(mStoreId).insert(ProductDatabase.Converter.productToMap(product), onSuccessListener);
    }

    /**
     * Provides an interface to the ViewModels to update the product in the database
     * @param product product object to be updated
     * @param onSuccessListener callback upon successful database update
     */
    public void update(Product product, OnSuccessListener onSuccessListener) {
        ProductDatabase.getInstance(mStoreId).update(ProductDatabase.Converter.productToMap(product), onSuccessListener);
    }

    /**
     * Provides an interface to the ViewModels to delete the product in the database
     * @param product product object to be deleted
     * @param completionListener callback on the completion listener to remove the listener to observe the changes on the object
     */
    public void delete(Product product, DatabaseReference.CompletionListener completionListener) {
        ProductDatabase.getInstance(mStoreId).delete(ProductDatabase.Converter.productToMap(product), completionListener);
    }

    /**
     * Moves the product elements in the local storage (ArrayList), used in product rearrangement
     * @param fromIndex original index of the product
     * @param toIndex the index to be swapped
     */
    public void move(int fromIndex, int toIndex) {
        if (fromIndex < toIndex)
            for (int i = fromIndex; i < toIndex; i++)
                Collections.swap(mProducts.getValue(), i, i + 1);
        else
            for (int i = fromIndex; i > toIndex; i--)
                Collections.swap(mProducts.getValue(), i, i - 1);
        notifyObservers();
    }

    // SORTER METHODS
    // Utilizes the comparators to sort the products
    public void sortNameAsc() {
        Collections.sort(mProducts.getValue(), Product.nameAscComparator);
        notifyObservers();
    }

    public void sortNameDesc() {
        Collections.sort(mProducts.getValue(), Product.nameDescComparator);
        notifyObservers();
    }

    public void sortPriceAsc() {
        Collections.sort(mProducts.getValue(), Product.priceAscComparator);
        notifyObservers();
    }

    public void sortPriceDesc() {
        Collections.sort(mProducts.getValue(), Product.priceDescComparator);
        notifyObservers();
    }

    public void sortInventoryAsc() {
        Collections.sort(mProducts.getValue(), Product.inventoryAscComparator);
        notifyObservers();
    }

    public void sortInventoryDesc() {
        Collections.sort(mProducts.getValue(), Product.inventoryDescComparator);
        notifyObservers();
    }

    public void sortCartAsc() {
        Collections.sort(mProducts.getValue(), Product.cartAscComparator);
        notifyObservers();
    }

    public void sortCartDesc() {
        Collections.sort(mProducts.getValue(), Product.cartDescComparator);
        notifyObservers();
    }
    // END SORTER METHODS
    // END OPERATIONS

    // ChildEventListeners
    // Listen to the Product child added to the local storage and observe the changes made thereafter
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

        // Get the changed product
        Product changedProduct = ProductDatabase.Converter.mapToProduct(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue());

        // Get the index of the changed product in the local storage (ArrayList) by comparing the productId
        int changedProductIndex = getProductIndexFromProductId(changedProduct.getId());

        // Get the original product from the ArrayList before updating it with the new value
        Product originalProduct = mProducts.getValue()
                .get(changedProductIndex);

        // Copy the cart quantity and extension from the original product to the changed product as these 2 values were not present in the database
        changedProduct.setCartQuantity(originalProduct.getCartQuantity());
        changedProduct.setCartExtension(originalProduct.getCartExtension());

        // Update the local storage with the updated product
        mProducts.getValue()
                .set(changedProductIndex, changedProduct);
        mChildEventListener.onChildChanged(dataSnapshot, s);
        notifyObservers();
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        // Get the removed product from the local storage (ArrayList)
        Map<String, Object> data = (Map<String, Object>) dataSnapshot.getValue();
        Product removedProduct = ProductDatabase.Converter.mapToProduct(dataSnapshot.getKey(), data);

        // Remove the removed product from the product ArrayList
        mProducts.getValue()
                .remove(getProductIndexFromProductId(removedProduct.getId()));

        ArrayList<String> productNames = new ArrayList<>();
        int cartPosition = 0;

        // Check if the current cart contains the removed product
        if ((cartPosition = getCartIndexFromProductId(removedProduct.getId())) != -1) {

            // Add the removed product names to a new ArrayList to notify the user that the cart was changed due to the removal of the product
            productNames.add(removedProduct.getName());

            // Remove the product from the cart ArrayList
            mCartItems.getValue()
                    .remove(cartPosition);
        }

        if (productNames.size() > 0)
            mCartInterface.notifyCartChanged(productNames);

        mChildEventListener.onChildRemoved(dataSnapshot);
        notifyObservers();
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        // Not used
        mChildEventListener.onChildMoved(dataSnapshot, s);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        // Not used
        mChildEventListener.onCancelled(databaseError);
    }
    // END ChildEventListeners

    /**
     * Get the cart index of the local storage (ArrayList) from the given productId
     * @param productId productId to be compared
     * @return cart index in the ArrayList
     */
    private int getCartIndexFromProductId(String productId) {
        for (int i = 0; i < mCartItems.getValue().size(); i++)
            if (mCartItems.getValue().get(i).getId().equals(productId))
                return i;
        return -1;
    }

    /**
     * Get the product index of the local storage (ArrayList) from the given productId
     * @param productId productId to be compared
     * @return product index in the ArrayList
     */
    private int getProductIndexFromProductId(String productId) {
        for (int i = 0; i < mProducts.getValue().size(); i++)
            if (mProducts.getValue().get(i).getId().equals(productId))
                return i;
        return -1;
    }

    /**
     * Get the product index from the given product object
     * @param product product object to be compared
     * @return product index in the ArrayList
     */
    public int getProductIndexFromProduct(Product product) {
        for (int i = 0; i < mProducts.getValue().size(); i++)
            if (product == mProducts.getValue().get(i)) // Compare the reference, not the value, therefore .equals() not needed
                return i;
        return -1;
    }

    /**
     * Notify the observers (View) on the changes made to the products and carts to update the View components dynamically
     */
    public void notifyObservers() {
        mProducts.setValue(mProducts.getValue());
        mCartItems.setValue(mCartItems.getValue());
    }

    // GETTER METHODS
    public MutableLiveData<ArrayList<Product>> getProducts() {
        return mProducts;
    }
    public MutableLiveData<ArrayList<Product>> getCartItems() {
        return mCartItems;
    }
    // END GETTER METHODS

    // Clear the instance of the repository and database upon logout to prepare for a new session
    public static void clearInstance() {
        sProductRepository = null;
        ProductDatabase.clearInstance();
    }
}
