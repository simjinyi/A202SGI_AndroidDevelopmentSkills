package com.example.pointofsales.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.model.Point;
import com.example.pointofsales.model.PointsRedeemedAndAwarded;
import com.example.pointofsales.model.Store;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.UserType;
import com.example.pointofsales.repository.PointRepository;
import com.example.pointofsales.repository.ProductRepository;
import com.example.pointofsales.repository.UserRepository;
import com.example.pointofsales.view.checkout.ScanListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class CheckoutViewModel extends ViewModel implements ChildEventListener {

    private MutableLiveData<Integer> mPointsRedeemedError;
    private MutableLiveData<Boolean> mScanUserNotFound;
    private MutableLiveData<Point> mPoint;
    private MutableLiveData<PointsRedeemedAndAwarded> mPointsRedeemedAndAwarded;

    private Store mStore;

    private PointRepository mPointRepository;
    private UserRepository mUserRepository;

    private ProductViewModel mProductViewModel;


    public CheckoutViewModel(ProductViewModel productViewModel) {

        mPointRepository = PointRepository.getInstance(UserViewModel.getUser(), this);
        mUserRepository = UserRepository.getInstance();

        mScanUserNotFound = new MutableLiveData<>();
        mScanUserNotFound.setValue(false);

        mStore = (Store) UserRepository.getInstance().getUser().getValue();

        mScanUserNotFound = new MutableLiveData<>();
        mScanUserNotFound.setValue(false);

        mPoint = mPointRepository.getPoint();
        mPointsRedeemedAndAwarded = mPointRepository.getPointsRedeemedAndAwarded();

        mPointsRedeemedError = new MutableLiveData<>();
        mPointsRedeemedError.setValue(null);

        mProductViewModel = productViewModel;
    }

    public void assignPoint(String userId) {
        mUserRepository.get(userId, UserType.CUSTOMER, new ScanListener() {
            @Override
            public void getUser(User user) {
                if (user == null) {
                    mScanUserNotFound.setValue(true);
                } else {
                    mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded());
                    mPoint.setValue(findPointByUser(user));
                }
            }
        });
    }
    
    public void removePoint() {
        mPointRepository.clearPoint();
    }

    public void redeemPointChanged(String pointsRedeemed) {
        if (pointsRedeemed.isEmpty())
            pointsRedeemed = "0";

        int pointsPerPrice = ((Store) UserViewModel.getUser()).getPointsPerPrice();
        float subTotal = mProductViewModel.getCart().getValue().getSubtotal();
        Integer pointsRedeemedParsed = Integer.parseInt(pointsRedeemed);
        int maxPointsAvailable = mPoint.getValue().getPoints();

        if (pointsRedeemedParsed == 0) {
            mProductViewModel.getCart().getValue().setDiscount(0.0f);
            mPointsRedeemedError.setValue(null);
            mProductViewModel.notifyCartObservers();
            return;
        }

        float discount = (float) pointsRedeemedParsed / pointsPerPrice;

        if (discount > subTotal) {
            mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded(0, 0));
            mProductViewModel.getCart().getValue().setDiscount(0.0f);
            mPointsRedeemedError.setValue((int) subTotal * pointsPerPrice);
            mProductViewModel.notifyCartObservers();
        } else if (maxPointsAvailable < pointsRedeemedParsed) {
            mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded(0, 0));
            mProductViewModel.getCart().getValue().setDiscount(0.0f);
            mPointsRedeemedError.setValue(mPoint.getValue().getPoints());
            mProductViewModel.notifyCartObservers();
        } else {

            mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded(pointsRedeemedParsed, 0));
            mProductViewModel.getCart().getValue().setDiscount(discount);
            mPointsRedeemedError.setValue(null);
            mProductViewModel.notifyCartObservers();
        }
    }

    public void clearPoint() {
        mPointRepository.clearPoint();
        mPointRepository.clearInstance();
    }

    public void clearScanUserNotFoundFlag() {
        mScanUserNotFound.setValue(false);
    }

    private Point findPointByUser(User user) {
        for (Point point : mPointRepository.getPoints().getValue())
            if (point.getUserId().equals(user.getId()))
                return point;

        Point point = new Point(user.getName(), user.getId(), mStore.getName(), mStore.getId(), 0);
        mPointRepository.insert(point, null);
        return point;
    }

    public LiveData<Point> getPoint() {
        return mPoint;
    }
    public LiveData<PointsRedeemedAndAwarded> getPointsRedeemedAndAwarded() {
        return mPointsRedeemedAndAwarded;
    }
    public LiveData<Boolean> getScanUserNotFound() {
        return mScanUserNotFound;
    }
    public LiveData<Integer> getPointsRedeemedError() {
        return mPointsRedeemedError;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
