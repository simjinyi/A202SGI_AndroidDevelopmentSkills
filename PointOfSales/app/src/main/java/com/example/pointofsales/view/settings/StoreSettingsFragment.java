package com.example.pointofsales.view.settings;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pointofsales.R;
import com.example.pointofsales.viewmodel.StoreSettingsViewModel;

public class StoreSettingsFragment extends Fragment {

    private StoreSettingsViewModel mViewModel;

    public static StoreSettingsFragment newInstance() {
        return new StoreSettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.store_settings_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(StoreSettingsViewModel.class);
        // TODO: Use the ViewModel
    }

}
