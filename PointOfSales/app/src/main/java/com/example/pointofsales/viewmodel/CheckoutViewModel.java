package com.example.pointofsales.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.model.Point;
import com.example.pointofsales.model.Store;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.UserType;
import com.example.pointofsales.repository.PointRepository;
import com.example.pointofsales.repository.UserRepository;
import com.example.pointofsales.view.checkout.ScanListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

public class CheckoutViewModel extends ViewModel {

    private MutableLiveData<Boolean> mScanUserNotFound;
    private MutableLiveData<Point> mPoint;

    private Store mStore;

    private PointRepository mPointRepository;
    private UserRepository mUserRepository;

    private ProductViewModel mProductViewModel;


    public CheckoutViewModel(ProductViewModel productViewModel) {

        mPointRepository = PointRepository.getInstance(UserViewModel.getUser(), new ChildEventListener() {
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
        });
        mUserRepository = UserRepository.getInstance();

        mScanUserNotFound = new MutableLiveData<>();
        mScanUserNotFound.setValue(false);

        mStore = (Store) UserRepository.getInstance().getUser().getValue();

        mScanUserNotFound = new MutableLiveData<>();
        mScanUserNotFound.setValue(false);

        mPoint = mPointRepository.getPoint();

        mProductViewModel = productViewModel;
    }

    public void assignPoint(String userId) {
        mUserRepository.get(userId, UserType.CUSTOMER, new ScanListener() {
            @Override
            public void getUser(User user) {
                if (user == null)
                    mScanUserNotFound.setValue(true);
                else
                    mPoint.setValue(findPointByUser(user));
            }
        });
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
    public LiveData<Boolean> getScanUserNotFound() {
        return mScanUserNotFound;
    }
}
