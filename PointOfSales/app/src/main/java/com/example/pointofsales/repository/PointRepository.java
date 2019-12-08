package com.example.pointofsales.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.pointofsales.database.PointDatabase;
import com.example.pointofsales.model.Point;
import com.example.pointofsales.model.PointsRedeemedAndAwarded;
import com.example.pointofsales.model.Store;
import com.example.pointofsales.model.User;
import com.example.pointofsales.view.checkout.UpdatePointInterface;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

/**
 * Acts as the intermediary between the ViewModels and the Database
 */
public class PointRepository implements ChildEventListener {

    private User mUser;

    // MutableLiveData being observed by the View, and updated dynamically
    private MutableLiveData<Point> mPoint;
    private MutableLiveData<PointsRedeemedAndAwarded> mPointsRedeemedAndAwarded;
    private MutableLiveData<ArrayList<Point>> mPoints;
    private UpdatePointInterface mUpdatePointInterface;

    private static PointRepository sPointRepository;

    private PointRepository(User user, UpdatePointInterface updatePointInterface) {

        mUser = user;
        mUpdatePointInterface = updatePointInterface;

        mPoints = new MutableLiveData<>();
        mPoints.setValue(new ArrayList<Point>());

        mPoint = new MutableLiveData<>();
        mPoint.setValue(null);

        mPointsRedeemedAndAwarded = new MutableLiveData<>();
        mPointsRedeemedAndAwarded.setValue(null);

        PointDatabase.getInstance().get(mUser, this);
    }

    public static PointRepository getInstance(User user, UpdatePointInterface updatePointInterface) {
        if (sPointRepository == null)
            sPointRepository = new PointRepository(user, updatePointInterface);
        return sPointRepository;
    }

    /**
     * Clear the member added for a transaction, called when the member was removed or the transaction was completed
     */
    public void clearPoint() {
        mPoint.setValue(null);
        mPointsRedeemedAndAwarded.setValue(null);
    }

    /**
     * Calls the PointDatabase to update the Point object
     * @param point point object to be updated
     * @param onSuccessListener callback on response from the database
     */
    public void insert(final Point point, final OnSuccessListener onSuccessListener) {
        PointDatabase.getInstance().insert(point, new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

                // Set the user inserted as the current member
                mPoint.setValue(point);

                if (onSuccessListener != null)
                    onSuccessListener.onSuccess(o);
            }
        });
    }

    /**
     * Update the Point object in the database
     * @param point point object to be updated
     * @param onSuccessListener callback when the operation succeeded
     */
    public void update(Point point, OnSuccessListener onSuccessListener) {
        PointDatabase.getInstance().update(PointDatabase.Converter.pointToMap(point), onSuccessListener);
    }

    /**
     * Update the user name in the Point database
     * @param userId userId to be updated
     * @param userName new name to be updated
     */
    public static void updateUserName(String userId, String userName) {
        PointDatabase.getInstance().updateUserName(userId, userName);
    }

    /**
     * Updates the store details in the Point database
     * @param storeId storeId to update
     * @param storeDetails details to be updated
     */
    public static void updateStoreDetails(String storeId, Store storeDetails) {
        PointDatabase.getInstance().updateStoreDetails(storeId, storeDetails);
    }

    /**
     * Notify the Observers that the data was changed and the View should be redrawn
     */
    public void notifyObservers() {
        mPoints.setValue(mPoints.getValue());
    }

    /**
     * Clear repository instance upon logout
     */
    public static void clearInstance() {
        sPointRepository = null;
        PointDatabase.clearInstance();
    }

    /**
     * Check if the point exists for a user or seller
     * @param user User object for checking
     * @param pointInterface callback when the database returns
     */
    public void check(User user, final PointInterface pointInterface) {
        PointDatabase.getInstance()
                .check(user, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        pointInterface.pointExistCallback(dataSnapshot.exists());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        pointInterface.pointExistCallback(false);
                    }
                });
    }

    // ChildEventListeners
    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        // Add the data into the local storage when the data was added or obtained to and from the database
        mPoints.getValue().add(PointDatabase.Converter.mapToPoint(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue()));
        notifyObservers();
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        // Get the updated Point object
        Point changedPoint = PointDatabase.Converter.mapToPoint(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue());

        // If the user was assigned for the transaction
        if (mPoint.getValue() != null) {
            if (changedPoint.getPointId().equals(mPoint.getValue().getPointId()))
                mPoint.setValue(changedPoint);

            // Callback onPointChanged to notify the classes that the point was changed
            mUpdatePointInterface.onPointChanged(changedPoint);

            // Update the local storage
            mPoints.getValue().set(getPointIndexFromPointId(changedPoint.getPointId()), changedPoint);
            notifyObservers();
        }
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        // Get the Point object removed from the database
        Point removedPoint = PointDatabase.Converter.mapToPoint(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue());

        // If the membership was assigned for the transaction
        if (mPoint.getValue() != null) {

            // Remove the Point object from the local storage
            mPoints.getValue().remove(getPointIndexFromPointId(removedPoint.getPointId()));
            notifyObservers();
            mUpdatePointInterface.onPointDeleted();
        }
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        // ignore
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        // ignore
    }
    // END ChildEventListener

    /**
     * Get the point index in the ArrayList given the pointId
     * @param pointId pointId to be searched
     * @return index of the Point object found, -1 if not found
     */
    private int getPointIndexFromPointId(String pointId) {
        for (int i = 0; i < mPoints.getValue().size(); i++)
            if (mPoints.getValue().get(i).getPointId().equals(pointId))
                return i;

        return -1;
    }

    // GETTERS for the MutableLiveData objects
    public MutableLiveData<Point> getPoint() { return mPoint; }
    public MutableLiveData<ArrayList<Point>> getPoints() {
        return mPoints;
    }
    public MutableLiveData<PointsRedeemedAndAwarded> getPointsRedeemedAndAwarded() {
        return mPointsRedeemedAndAwarded;
    }
    // END GETTERS
}
