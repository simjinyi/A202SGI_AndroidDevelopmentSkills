package com.example.pointofsales.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.model.User;
import com.example.pointofsales.repository.CartRepository;
import com.example.pointofsales.repository.MembershipRepository;
import com.example.pointofsales.repository.PointRepository;
import com.example.pointofsales.repository.ProductRepository;
import com.example.pointofsales.repository.TransactionRepository;
import com.example.pointofsales.repository.UserRepository;

public class UserViewModel extends ViewModel {

    private MutableLiveData<User> mUser;

    private UserRepository mUserRepository;

    public UserViewModel() {
        mUserRepository = UserRepository.getInstance();
        mUser = mUserRepository.getUser();
    }

    public static boolean isLoggedIn() {
        return UserRepository.getInstance().getUser().getValue().isLoggedIn();
    }

    public static String getUserId() {
        return UserRepository.getInstance().getUser().getValue().getId();
    }

    public static User getUser() {
        return UserRepository.getInstance().getUser().getValue();
    }

    public static void logout() {
        CartRepository.clearInstance();
        ProductRepository.clearInstance();
        UserRepository.clearInstance();
        PointRepository.clearInstance();
        TransactionRepository.clearInstance();
        MembershipRepository.clearInstance();
    }

    public LiveData<User> getUserLiveData() {
        return mUser;
    }
}
