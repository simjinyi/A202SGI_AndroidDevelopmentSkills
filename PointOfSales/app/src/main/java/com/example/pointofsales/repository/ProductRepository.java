package com.example.pointofsales.repository;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.annotation.NonNull;

import com.example.pointofsales.model.Product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ProductRepository {

    public static final int DUPLICATED_NAME = 1;
    public static final int UNKNOWN_ERROR = 2;

    private static ProductRepository productRepository;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    private ProductRepository() {
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("product");
    }

    public static ProductRepository getInstance() {
        if (productRepository == null)
            productRepository = new ProductRepository();
        return productRepository;
    }

    public void addValueEventListener(String storeId, ValueEventListener valueEventListener) {
        ref.child(storeId).addValueEventListener(valueEventListener);
    }

    public void insertIntoDatabase(final Map<String, Object> product, final String storeId, final OnSuccessListener onSuccessListener) {
        ref.child(storeId)
                .orderByChild("nameValidate")
                .equalTo(product.get("name").toString().toLowerCase())
                .addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    ref.child(storeId).push().setValue(product).addOnSuccessListener(onSuccessListener);
                else
                    onSuccessListener.onSuccess(DUPLICATED_NAME);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onSuccessListener.onSuccess(UNKNOWN_ERROR);
            }
        });
    }

    public static Map<String, Object> productToMap(Product product) {

        Map<String, Object> hashMap = new HashMap<>();

        if (product.getImage() != null) {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            product.getImage().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

            hashMap.put("image", Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
        }

        hashMap.put("name", product.getName());
        hashMap.put("nameValidate", product.getName().toLowerCase());
        hashMap.put("price", product.getPrice());
        hashMap.put("inventoryQuantity", product.getInventoryQuantity());
        hashMap.put("pointPerItem", product.getPointPerItem());
        hashMap.put("isDisabled", product.isDisabled());
        hashMap.put("totalSales", product.getTotalSales());

        return hashMap;
    }

    public static Product mapToProduct(String id, Map<String, Object> map) {

        Product product = new Product();

        if (map.get("image") != null) {
            byte[] decodedString = Base64.decode(map.get("image").toString(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            product.setImage(decodedByte);
        } else {
            product.setImage(null);
        }

        product.setId(id);
        product.setName(map.get("name").toString());
        product.setPrice(Float.parseFloat(map.get("price").toString()));
        product.setInventoryQuantity(Integer.parseInt(map.get("inventoryQuantity").toString()));
        product.setPointPerItem(Integer.parseInt(map.get("pointPerItem").toString()));
        product.setDisabled(Boolean.parseBoolean(map.get("isDisabled").toString()));
        product.setTotalSales(Integer.parseInt(map.get("totalSales").toString()));

        return product;
    }
}
