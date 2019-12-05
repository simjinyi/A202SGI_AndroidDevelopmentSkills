package com.example.pointofsales.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.pointofsales.database.PointDatabase;
import com.example.pointofsales.model.Point;
import com.example.pointofsales.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Map;

public class PointRepository implements ChildEventListener {

    private User mUser;
    private ChildEventListener mChildEventListener;
    private MutableLiveData<Point> mPoint;
    private MutableLiveData<ArrayList<Point>> mPoints;

    private static PointRepository sPointRepository;

    private PointRepository(User user, ChildEventListener childEventListener) {

        mUser = user;
        mChildEventListener = childEventListener;

        mPoints = new MutableLiveData<>();
        mPoints.setValue(new ArrayList<Point>());

        mPoint = new MutableLiveData<>();
        mPoint.setValue(null);

        PointDatabase.getInstance().get(mUser, this);
    }

    public static PointRepository getInstance(User user, ChildEventListener childEventListener) {
        if (sPointRepository == null)
            sPointRepository = new PointRepository(user, childEventListener);
        return sPointRepository;
    }

    public void clearPoint() {
        mPoint.setValue(null);
    }

    public void insert(Point point, OnSuccessListener onSuccessListener) {
        PointDatabase.getInstance().insert(PointDatabase.Converter.pointToMap(point), onSuccessListener);
    }

    public void update(Point point, OnSuccessListener onSuccessListener) {
        PointDatabase.getInstance().update(PointDatabase.Converter.pointToMap(point), onSuccessListener);
    }

    public void notifyObservers() {
        mPoints.setValue(mPoints.getValue());
    }

    public static void clearInstance() {
        sPointRepository = null;
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

        if (changedPoint.getPointId().equals(mPoint.getValue().getPointId()))
            mPoint.setValue(changedPoint);

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

    public MutableLiveData<Point> getPoint() { return mPoint; }
    public MutableLiveData<ArrayList<Point>> getPoints() {
        return mPoints;
    }
}
