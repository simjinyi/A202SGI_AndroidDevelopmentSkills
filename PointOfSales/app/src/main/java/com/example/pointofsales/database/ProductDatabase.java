package com.example.pointofsales.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.pointofsales.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * ProductDatabase Singleton Class
 */
public class ProductDatabase {

    private static final String PRODUCT_COLLECTION = "product";
    private static ProductDatabase sProductDatabase;

    private String mStoreId;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private ProductDatabase(String storeId) {
        mStoreId = storeId;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference(PRODUCT_COLLECTION);
    }

    public static ProductDatabase getInstance(String storeId) {
        if (sProductDatabase == null)
            sProductDatabase = new ProductDatabase(storeId);
        return sProductDatabase;
    }

    /**
     * Check if there's at least one product exists for the seller
     * @param valueEventListener callback on result from the database
     */
    public void check(ValueEventListener valueEventListener) {
        mDatabaseReference.orderByChild("storeId")
                .equalTo(mStoreId)
                .addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Get the product from the database for the seller
     * @param childEventListener callback for as the database returns data and listens to any changes
     */
    public void get(ChildEventListener childEventListener) {
        mDatabaseReference.orderByChild("storeId")
                .equalTo(mStoreId)
                .addChildEventListener(childEventListener);
    }

    /**
     * Insert product into database
     * @param product product to be inserted
     * @param onSuccessListener callback listener on successful operation
     */
    public void insert(Map<String, Object> product, OnSuccessListener onSuccessListener) {
        mDatabaseReference.push()
                .setValue(product)
                .addOnSuccessListener(onSuccessListener);
    }

    /**
     * Update product in the database
     * @param product product to be updated
     * @param onSuccessListener callback listener on successful operation
     */
    public void update(Map<String, Object> product, OnSuccessListener onSuccessListener) {
        mDatabaseReference
                .child(product.get("id").toString())
                .setValue(product)
                .addOnSuccessListener(onSuccessListener);
    }

    /**
     * Delete product from the database
     * @param product product to be deleted
     * @param completionListener callback to remove listener
     */
    public void delete(Map<String, Object> product, DatabaseReference.CompletionListener completionListener) {
        mDatabaseReference.child(product.get("id").toString()).removeValue(completionListener);
    }

    /**
     * Converter class to ease database operation
     */
    public static class Converter {

        /**
         * Convert Product object to Map<String, Object>
         * @param product product to be converted
         * @return converted map object
         */
        public static Map<String, Object> productToMap(Product product) {

            Map<String, Object> hashMap = new HashMap<>();

            // If the product image is not null, convert the image into Base64 to be saved in the database
            if (product.getImage() != null) {

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                product.getImage().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

                hashMap.put("image", Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
            }

            hashMap.put("id", product.getId());
            hashMap.put("name", product.getName());
            hashMap.put("nameValidate", product.getName().toLowerCase());
            hashMap.put("price", product.getPrice());
            hashMap.put("inventoryQuantity", product.getInventoryQuantity());
            hashMap.put("pointPerItem", product.getPointPerItem());
            hashMap.put("isDisabled", product.isDisabled());
            hashMap.put("totalSales", product.getTotalSales());
            hashMap.put("storeId", product.getStoreId());

            return hashMap;
        }

        /**
         * Convert Map<String, Object> object into Product object
         * @param productId productId from the database
         * @param map map to be converted
         * @return converted Point object
         */
        public static Product mapToProduct(String productId, Map<String, Object> map) {

            Product product = new Product();

            // If the image is not null, decode the image from Base64 and assign into the Product object
            if (map.get("image") != null) {
                byte[] decodedString = Base64.decode(map.get("image").toString(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                product.setImage(decodedByte);
            } else {
                product.setImage(null);
            }

            product.setId(productId);
            product.setName(map.get("name").toString());
            product.setPrice(Float.parseFloat(map.get("price").toString()));
            product.setInventoryQuantity(Integer.parseInt(map.get("inventoryQuantity").toString()));
            product.setPointPerItem(Integer.parseInt(map.get("pointPerItem").toString()));
            product.setDisabled(Boolean.parseBoolean(map.get("isDisabled").toString()));
            product.setTotalSales(Integer.parseInt(map.get("totalSales").toString()));

            return product;
        }
    }

    /**
     * Clear the database instance on logout
     */
    public static void clearInstance() {
        sProductDatabase = null;
    }
}
