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

/**
 * PointDatabase Singleton Class
 */
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

    /**
     * Check if there's the store contains a member or the customer is a member of the stores
     * @param user user to be cheked
     * @param valueEventListener callback from the database
     */
    public void check(User user, ValueEventListener valueEventListener) {
        if (user.getType().equals(UserType.SELLER))
            mDatabaseReference.orderByChild("storeId")
                    .equalTo(user.getId())
                    .addListenerForSingleValueEvent(valueEventListener);
        else
            mDatabaseReference.orderByChild("userId")
                    .equalTo(user.getId())
                    .addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Get the point data from the database
     * @param user points belonging to the user
     * @param childEventListener callback from the database
     */
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

    /**
     * Insert a new point object into the database, whereby a new member was consider to be created for a seller
     * @param point point object to be inserted
     * @param onSuccessListener callback from the database to indicate that the operation is successful
     */
    public void insert(Point point, OnSuccessListener onSuccessListener) {

        // Get database key to be assigned to the Point object
        String key = mDatabaseReference.push().getKey();
        point.setPointId(key);

        Map<String, Object> map = PointDatabase.Converter.pointToMap(point);

        mDatabaseReference.child(key)
                .setValue(map)
                .addOnSuccessListener(onSuccessListener);
    }

    /**
     * Update the point object in the database, for instance when the point for a user changes
     * @param point point object to be updated
     * @param onSuccessListener callback from the database to indicate that the operation is successful
     */
    public void update(Map<String, Object> point, OnSuccessListener onSuccessListener) {
        mDatabaseReference.child(point.get("pointId").toString())
                .setValue(point)
                .addOnSuccessListener(onSuccessListener);
    }

    /**
     * Update the user name in the point objects when the user name was changed
     * @param userId userId of the user with the user name changed
     * @param userName updated name
     */
    public void updateUserName(String userId, final String userName) {
        mDatabaseReference.orderByChild("userId")
                .equalTo(userId) // Compare against the userId passed
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // Loop through to update all the data points
                        for (DataSnapshot user : dataSnapshot.getChildren())
                            mDatabaseReference.child(user.getKey()).child("userName").setValue(userName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // ignore
                    }
                });
    }

    /**
     * Update the store details in the point objects when the store details was updated
     * @param storeId storeId for the objects to be updated
     * @param storeDetails data to update
     */
    public void updateStoreDetails(String storeId, final Store storeDetails) {
        mDatabaseReference.orderByChild("storeId")
                .equalTo(storeId) // Compare against the storeId passed
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // Loop through to update all the data points
                        for (DataSnapshot store : dataSnapshot.getChildren()) {
                            mDatabaseReference.child(store.getKey()).child("storeName").setValue(storeDetails.getName());
                            mDatabaseReference.child(store.getKey()).child("storeAddress").setValue(storeDetails.getAddress());
                            mDatabaseReference.child(store.getKey()).child("storePointsPerPrice").setValue(storeDetails.getPointsPerPrice());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // ignore
                    }
                });
    }

    /**
     * Converter inner class to convert between Java object and map to help in database operations
     */
    public static class Converter {

        /**
         * Convert Point object into Map<String, Object> object for database insertion or update
         * @param point Point object
         * @return converted Point object into Map<Sting, Object>
         */
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

        /**
         * Convert the Map<String, Object> to Point object
         * @param pointId id returned from the database
         * @param map map value returned from the database
         * @return Point object
         */
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

    // Clear the instance after logging out to not persist the data for other users
    public static void clearInstance() {
        sPointDatabase = null;
    }
}
