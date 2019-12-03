package com.example.pointofsales.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class StoreAccountViewModelFactory implements ViewModelProvider.Factory {

    private String mStoreId;

    public StoreAccountViewModelFactory(String storeId) {
        mStoreId = storeId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new StoreAccountViewModel(mStoreId);
    }
}