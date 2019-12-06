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

import java.util.ArrayList;
import java.util.Map;

public class MembershipRepository implements ChildEventListener {

    private User mUser;
    private MutableLiveData<ArrayList<Point>> mPoints;

    private static MembershipRepository sMembershipRepository;

    private MembershipRepository(User user) {
        mUser = user;

        mPoints = new MutableLiveData<>();
        mPoints.setValue(new ArrayList<Point>());

        PointDatabase.getInstance().get(user, this);
    }

    public static MembershipRepository getInstance(User user) {
        if (sMembershipRepository == null)
            sMembershipRepository = new MembershipRepository(user);
        return sMembershipRepository;
    }

    public MutableLiveData<ArrayList<Point>> getPoints() {
        return mPoints;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        mPoints.getValue().add(PointDatabase.Converter.mapToPoint(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue()));
        notifyObservers();
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Point changedPoint = PointDatabase.Converter.mapToPoint(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue());
        mPoints.getValue().set(getPointIndexFromPointId(changedPoint.getPointId()), changedPoint);
        notifyObservers();
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        Point removedPoint = PointDatabase.Converter.mapToPoint(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue());
        mPoints.getValue().remove(getPointIndexFromPointId(removedPoint.getPointId()));
        notifyObservers();
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    private int getPointIndexFromPointId(String pointId) {
        for (int i = 0; i < mPoints.getValue().size(); i++)
            if (mPoints.getValue().get(i).getPointId().equals(pointId))
                return i;

        return -1;
    }

    public void notifyObservers() {
        mPoints.setValue(mPoints.getValue());
    }
}
