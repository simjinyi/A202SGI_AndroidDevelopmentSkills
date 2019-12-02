package com.example.pointofsales.database;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pointofsales.model.Product;
import com.example.pointofsales.model.Store;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.UserType;
import com.example.pointofsales.view.login.LoginInterface;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class UserDatabase {

    private static final String USER_COLLECTION = "user";
    private static UserDatabase sUserDatabase;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private UserDatabase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference(USER_COLLECTION);
    }

    public static UserDatabase getInstance() {
        if (sUserDatabase == null)
            sUserDatabase = new UserDatabase();
        return sUserDatabase;
    }

    public void get(String username, final String password, final LoginInterface loginInterface) {
        mDatabaseReference.orderByChild("email")
                .equalTo(username)
                .limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        loginInterface.onLogin(true, dataSnapshot, password);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loginInterface.onLogin(false, null, password);
                    }
                });
    }

    public void insert(Map<String, Object> user, OnSuccessListener onSuccessListener) {
        mDatabaseReference.push()
                .setValue(user)
                .addOnSuccessListener(onSuccessListener);
    }

    public void update(Map<String, Object> user, OnSuccessListener onSuccessListener) {
        mDatabaseReference.child(user.get("id").toString())
                .setValue(user)
                .addOnSuccessListener(onSuccessListener);
    }

//    public void delete(Map<String, Object> product, ChildEventListener childEventListener) {
//        mDatabaseReference.child(product.get("id").toString()).removeEventListener(childEventListener);
//    }

    public static class Converter {
        public static Map<String, Object> userToMap(User user) {

            Map<String, Object> hashMap = new HashMap<>();

            hashMap.put("id", user.getId());
            hashMap.put("email", user.getEmail());
            hashMap.put("name", user.getName());
            hashMap.put("password", user.getPassword());
            hashMap.put("type", user.getType().name());

            if (user instanceof Store) {
                hashMap.put("address", ((Store) user).getAddress());
                hashMap.put("pointsPerPrice", ((Store) user).getPointsPerPrice());
            }

            return hashMap;
        }

        public static User mapToUser(String userId, Map<String, Object> map) {

            User user = map.get("type").toString().equals("SELLER") ? new Store() : new User();

            user.setId(userId);
            user.setName(map.get("name").toString());
            user.setEmail(map.get("email").toString());
            user.setPassword(map.get("password").toString());

            if (user instanceof Store) {
                user.setType(UserType.SELLER);
                ((Store) user).setAddress(map.get("address").toString());
                ((Store) user).setPointsPerPrice(Integer.parseInt(map.get("pointsPerPrice").toString()));
            } else {
                user.setType(UserType.CUSTOMER);
            }

            return user;
        }
    }
}
