package com.example.pointofsales.database;

import com.example.pointofsales.model.Point;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.UserType;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public static class Converter {
        public static Map<String, Object> pointToMap(Point point) {

            Map<String, Object> hashMap = new HashMap<>();

            hashMap.put("pointId", point.getPointId());
            hashMap.put("userId", point.getUserId());
            hashMap.put("userName", point.getUserName());
            hashMap.put("storeId", point.getStoreId());
            hashMap.put("storeName", point.getStoreName());
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
            point.setPoints(Integer.parseInt(map.get("points").toString()));

            return point;
        }
    }

    public static void clearInstance() {
        sPointDatabase = null;
    }
}
