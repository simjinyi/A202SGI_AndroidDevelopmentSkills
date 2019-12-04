package com.example.pointofsales.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class UserAccountViewModelFactory implements ViewModelProvider.Factory {

    private String mUserId;

    public UserAccountViewModelFactory(String userId) {
        mUserId = userId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new UserAccountViewModel(mUserId);
    }
}
