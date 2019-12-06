package com.example.pointofsales.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Point;
import com.example.pointofsales.model.sort.MembershipSort;
import com.example.pointofsales.model.state.PointLoadState;
import com.example.pointofsales.repository.MembershipRepository;
import com.example.pointofsales.repository.PointInterface;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class MembershipViewModel extends ViewModel implements PointInterface, ChildEventListener {

    private MutableLiveData<ArrayList<Point>> mPoints;
    private MutableLiveData<PointLoadState> mPointLoadState;

    private MembershipRepository mMembershipRepository;

    private MembershipSort mMembershipSort;

    public MembershipViewModel() {
        mMembershipRepository = MembershipRepository.getInstance(UserViewModel.getUser(), this);
        mPoints = mMembershipRepository.getPoints();

        mMembershipSort = new MembershipSort();

        mPointLoadState = new MutableLiveData<>();
        mPointLoadState.setValue(PointLoadState.LOADING);
        checkPointExists();
    }

    private void checkPointExists() {
        mMembershipRepository.checkPointExists(UserViewModel.getUser(), this);
    }

    public LiveData<ArrayList<Point>> getPoints() {
        return mPoints;
    }
    public LiveData<PointLoadState> getPointLoadState() {
        return mPointLoadState;
    }

    @Override
    public void pointExistCallback(boolean existence) {
        mPointLoadState.setValue(existence ? PointLoadState.LOADED : PointLoadState.NO_POINT);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        checkPointExists();
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        checkPointExists();
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        checkPointExists();
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

    public int sort() {
        switch (mMembershipSort.next()) {
            case STORE_NAME_ASC:
                mMembershipRepository.sortStoreNameAsc();
                return R.string.storeNameAscending;
            case STORE_NAME_DESC:
                mMembershipRepository.sortStoreNameDesc();
                return R.string.storeNameDescending;
            case CUSTOMER_NAME_ASC:
                mMembershipRepository.sortCustomerNameAsc();
                return R.string.customerNameAscending;
            case CUSTOMER_NAME_DESC:
                mMembershipRepository.sortCustomerNameDesc();
                return R.string.customerNameDescending;
            case POINT_ASC:
                mMembershipRepository.sortPointAsc();
                return R.string.pointAscending;
            case POINT_DESC:
                mMembershipRepository.sortPointDesc();
                return R.string.pointDescending;
            case POINTS_PER_PRICE_ASC:
                mMembershipRepository.sortPointsPerPriceAsc();
                return R.string.pointsPerPriceAscending;
            default:
                mMembershipRepository.sortPointsPerPriceDesc();
                return R.string.pointsPerPriceDescending;
        }
    }
}
