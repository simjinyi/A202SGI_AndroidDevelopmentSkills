package com.example.pointofsales.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.model.Point;
import com.example.pointofsales.repository.MembershipRepository;
import com.example.pointofsales.repository.PointRepository;
import com.example.pointofsales.view.checkout.UpdatePointInterface;

import java.util.ArrayList;

public class MembershipViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Point>> mPoints;

    private MembershipRepository mMembershipRepository;

    public MembershipViewModel() {
        mMembershipRepository = MembershipRepository.getInstance(UserViewModel.getUser());
        mPoints = mMembershipRepository.getPoints();
    }

    public LiveData<ArrayList<Point>> getPoints() {
        return mPoints;
    }
}
