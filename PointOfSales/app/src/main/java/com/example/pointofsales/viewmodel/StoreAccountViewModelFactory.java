package com.example.pointofsales.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class StoreAccountViewModelFactory implements ViewModelProvider.Factory {

    private CheckoutViewModel mCheckoutViewModel;

    public StoreAccountViewModelFactory(CheckoutViewModel checkoutViewModel) {
        mCheckoutViewModel = checkoutViewModel;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new StoreAccountViewModel(mCheckoutViewModel);
    }
}