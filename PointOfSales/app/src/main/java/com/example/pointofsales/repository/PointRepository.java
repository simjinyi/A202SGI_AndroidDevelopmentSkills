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

import java.util.ArrayList;
import java.util.Map;

public class PointRepository implements ChildEventListener {

    private User mUser;
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

    public void clearPoint() {
        mPoint.setValue(null);
        mPointsRedeemedAndAwarded.setValue(null);
    }

    public void insert(Point point, OnSuccessListener onSuccessListener) {
        PointDatabase.getInstance().insert(PointDatabase.Converter.pointToMap(point), onSuccessListener);
    }

    public void update(Point point, OnSuccessListener onSuccessListener) {
        PointDatabase.getInstance().update(PointDatabase.Converter.pointToMap(point), onSuccessListener);
    }

    public static void updateUserName(String userId, String userName) {
        PointDatabase.getInstance().updateUserName(userId, userName);
    }

    public static void updateStoreDetails(String storeId, Store storeDetails) {
        PointDatabase.getInstance().updateStoreDetails(storeId, storeDetails);
    }

    public void notifyObservers() {
        mPoints.setValue(mPoints.getValue());
    }

    public static void clearInstance() {
        sPointRepository = null;
        PointDatabase.clearInstance();
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        mPoints.getValue().add(PointDatabase.Converter.mapToPoint(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue()));
        notifyObservers();
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        Point changedPoint = PointDatabase.Converter.mapToPoint(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue());

        if (mPoint.getValue() != null) {
            if (changedPoint.getPointId().equals(mPoint.getValue().getPointId()))
                mPoint.setValue(changedPoint);

            if (mPoint.getValue().getPoints() < mPointsRedeemedAndAwarded.getValue().getRedeemedPoint()) {
                mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded(mPoint.getValue().getPoints(), mPointsRedeemedAndAwarded.getValue().getPointAwarded()));
                mUpdatePointInterface.onPointChanged(changedPoint);
            }

            mPoints.getValue().set(getPointIndexFromPointId(changedPoint.getPointId()), changedPoint);
            notifyObservers();
        }
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        Point removedPoint = PointDatabase.Converter.mapToPoint(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue());

        if (mPoint.getValue() != null) {
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

    private int getPointIndexFromPointId(String pointId) {
        for (int i = 0; i < mPoints.getValue().size(); i++)
            if (mPoints.getValue().get(i).getPointId().equals(pointId))
                return i;

        return -1;
    }

    public MutableLiveData<Point> getPoint() { return mPoint; }
    public MutableLiveData<ArrayList<Point>> getPoints() {
        return mPoints;
    }
    public MutableLiveData<PointsRedeemedAndAwarded> getPointsRedeemedAndAwarded() {
        return mPointsRedeemedAndAwarded;
    }
}
