package com.example.pointofsales.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.pointofsales.database.PointDatabase;
import com.example.pointofsales.model.Point;
import com.example.pointofsales.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class MembershipRepository implements ChildEventListener {

    private User mUser;
    private ChildEventListener mChildEventListener;
    private MutableLiveData<ArrayList<Point>> mPoints;

    private static MembershipRepository sMembershipRepository;

    private MembershipRepository(User user, ChildEventListener childEventListener) {
        mUser = user;

        mPoints = new MutableLiveData<>();
        mPoints.setValue(new ArrayList<Point>());
        mChildEventListener = childEventListener;

        PointDatabase.getInstance().get(user, this);
    }

    public static MembershipRepository getInstance(User user, ChildEventListener childEventListener) {
        if (sMembershipRepository == null)
            sMembershipRepository = new MembershipRepository(user, childEventListener);
        return sMembershipRepository;
    }

    public MutableLiveData<ArrayList<Point>> getPoints() {
        return mPoints;
    }

    public void checkPointExists(User user, final PointInterface pointInterface) {
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

    public void sortStoreNameAsc() {
        Collections.sort(mPoints.getValue(), Point.storeNameAsc);
        notifyObservers();
    }

    public void sortStoreNameDesc() {
        Collections.sort(mPoints.getValue(), Point.storeNameDesc);
        notifyObservers();
    }

    public void sortCustomerNameAsc() {
        Collections.sort(mPoints.getValue(), Point.customerNameAsc);
        notifyObservers();
    }

    public void sortCustomerNameDesc() {
        Collections.sort(mPoints.getValue(), Point.customerNameDesc);
        notifyObservers();
    }

    public void sortPointAsc() {
        Collections.sort(mPoints.getValue(), Point.pointAsc);
        notifyObservers();
    }

    public void sortPointDesc() {
        Collections.sort(mPoints.getValue(), Point.pointDesc);
        notifyObservers();
    }

    public void sortPointsPerPriceAsc() {
        Collections.sort(mPoints.getValue(), Point.pointsPerPriceAsc);
        notifyObservers();
    }

    public void sortPointsPerPriceDesc() {
        Collections.sort(mPoints.getValue(), Point.pointsPerPriceDesc);
        notifyObservers();
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        mPoints.getValue().add(PointDatabase.Converter.mapToPoint(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue()));
        mChildEventListener.onChildAdded(dataSnapshot, s);
        notifyObservers();
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Point changedPoint = PointDatabase.Converter.mapToPoint(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue());
        mPoints.getValue().set(getPointIndexFromPointId(changedPoint.getPointId()), changedPoint);
        mChildEventListener.onChildChanged(dataSnapshot, s);
        notifyObservers();
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        Point removedPoint = PointDatabase.Converter.mapToPoint(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue());
        mPoints.getValue().remove(getPointIndexFromPointId(removedPoint.getPointId()));
        mChildEventListener.onChildRemoved(dataSnapshot);
        notifyObservers();
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        mChildEventListener.onChildMoved(dataSnapshot, s);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        mChildEventListener.onCancelled(databaseError);
    }

    private int getPointIndexFromPointId(String pointId) {
        for (int i = 0; i < mPoints.getValue().size(); i++)
            if (mPoints.getValue().get(i).getPointId().equals(pointId))
                return i;

        return -1;
    }

    public static void clearInstance() {
        sMembershipRepository = null;
        PointDatabase.clearInstance();
    }

    public void notifyObservers() {
        mPoints.setValue(mPoints.getValue());
    }
}
