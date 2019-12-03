package com.example.pointofsales.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.pointofsales.model.User;
import com.example.pointofsales.repository.UserRepository;

public class UserViewModel extends ViewModel {
    public static boolean isLoggedIn() {
        return UserRepository.getInstance().getUser().getValue().isLoggedIn();
    }

    public static String getUserId() {
        return UserRepository.getInstance().getUser().getValue().getId();
    }

    public static User getUser() {
        return UserRepository.getInstance().getUser().getValue();
    }
}
