package com.example.pointofsales.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.example.pointofsales.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ProductDatabase {

    private static final String PRODUCT = "product";
    private static ProductDatabase sProductDatabase;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private ProductDatabase(String storeId) {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference(PRODUCT).child(storeId);
    }

    public static ProductDatabase getInstance(String storeId) {
        if (sProductDatabase == null)
            sProductDatabase = new ProductDatabase(storeId);
        return sProductDatabase;
    }

    public void get(ChildEventListener childEventListener) {
        mDatabaseReference.addChildEventListener(childEventListener);
    }

    public void insert(Map<String, Object> product, OnSuccessListener onSuccessListener) {
        mDatabaseReference.push()
                .setValue(product)
                .addOnSuccessListener(onSuccessListener);
    }

    public void update(Map<String, Object> product, OnSuccessListener onSuccessListener) {
        mDatabaseReference.child(product.get("id").toString())
                .setValue(product)
                .addOnSuccessListener(onSuccessListener);
    }

    public void delete(Map<String, Object> product, ChildEventListener childEventListener) {
        mDatabaseReference.child(product.get("id").toString()).removeEventListener(childEventListener);
    }

    public static class Converter {
        public static Map<String, Object> productToMap(Product product) {

            Map<String, Object> hashMap = new HashMap<>();

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

            return hashMap;
        }

        public static Product mapToProduct(String productId, Map<String, Object> map) {

            Product product = new Product();

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
}
