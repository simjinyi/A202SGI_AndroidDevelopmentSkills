package com.example.pointofsales.database;

import androidx.annotation.NonNull;

import com.example.pointofsales.model.Point;
import com.example.pointofsales.model.Store;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.UserType;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PointDatabase {

    private static final String POINT_COLLECTION = "point";
    private static PointDatabase sPointDatabase;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private PointDatabase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference(POINT_COLLECTION);
    }

    public static PointDatabase getInstance() {
        if (sPointDatabase == null)
            sPointDatabase = new PointDatabase();
        return sPointDatabase;
    }

    public void check(String userId, ValueEventListener valueEventListener) {
        mDatabaseReference.orderByChild("userId")
                .equalTo(userId)
                .addListenerForSingleValueEvent(valueEventListener);
    }

    public void get(User user, ChildEventListener childEventListener) {
        if (user.getType().equals(UserType.SELLER))
            mDatabaseReference.orderByChild("storeId")
                    .equalTo(user.getId())
                    .addChildEventListener(childEventListener);
        else
            mDatabaseReference.orderByChild("userId")
                    .equalTo(user.getId())
                    .addChildEventListener(childEventListener);
    }

    public void insert(Map<String, Object> point, OnSuccessListener onSuccessListener) {
        mDatabaseReference.push()
                .setValue(point)
                .addOnSuccessListener(onSuccessListener);
    }

    public void update(Map<String, Object> point, OnSuccessListener onSuccessListener) {
        mDatabaseReference.child(point.get("pointId").toString())
                .setValue(point)
                .addOnSuccessListener(onSuccessListener);
    }

    public void updateUserName(String userId, final String userName) {
        mDatabaseReference.orderByChild("userId")
                .equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot user : dataSnapshot.getChildren())
                            mDatabaseReference.child(user.getKey()).child("userName").setValue(userName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void updateStoreDetails(String storeId, final Store storeDetails) {
        mDatabaseReference.orderByChild("storeId")
                .equalTo(storeId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot store : dataSnapshot.getChildren()) {
                            mDatabaseReference.child(store.getKey()).child("storeName").setValue(storeDetails.getName());
                            mDatabaseReference.child(store.getKey()).child("storeAddress").setValue(storeDetails.getAddress());
                            mDatabaseReference.child(store.getKey()).child("storePointsPerPrice").setValue(storeDetails.getPointsPerPrice());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public static class Converter {
        public static Map<String, Object> pointToMap(Point point) {

            Map<String, Object> hashMap = new HashMap<>();

            hashMap.put("pointId", point.getPointId());
            hashMap.put("userId", point.getUserId());
            hashMap.put("userName", point.getUserName());
            hashMap.put("storeId", point.getStoreId());
            hashMap.put("storeName", point.getStoreName());
            hashMap.put("storeAddress", point.getStoreAddress());
            hashMap.put("storePointsPerPrice", point.getStorePointsPerPrice());
            hashMap.put("points", point.getPoints());

            return hashMap;
        }

        public static Point mapToPoint(String pointId, Map<String, Object> map) {

            Point point = new Point();

            point.setPointId(pointId);
            point.setUserId(map.get("userId").toString());
            point.setUserName(map.get("userName").toString());
            point.setStoreId(map.get("storeId").toString());
            point.setStoreName(map.get("storeName").toString());
            point.setStoreAddress(map.get("storeAddress").toString());
            point.setStorePointsPerPrice(Integer.parseInt(map.get("storePointsPerPrice").toString()));
            point.setPoints(Integer.parseInt(map.get("points").toString()));

            return point;
        }
    }

    public static void clearInstance() {
        sPointDatabase = null;
    }
}
