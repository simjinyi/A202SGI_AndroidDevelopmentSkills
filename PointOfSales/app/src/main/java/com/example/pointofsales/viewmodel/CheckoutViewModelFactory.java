package com.example.pointofsales.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CheckoutViewModelFactory implements ViewModelProvider.Factory  {

    private ProductViewModel mProductViewModel;

    public CheckoutViewModelFactory(ProductViewModel productViewModel) {
        mProductViewModel = productViewModel;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CheckoutViewModel(mProductViewModel);
    }
}
