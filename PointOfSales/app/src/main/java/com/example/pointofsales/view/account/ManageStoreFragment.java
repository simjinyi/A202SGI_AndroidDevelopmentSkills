package com.example.pointofsales.view.account;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;

import com.example.pointofsales.R;
import com.example.pointofsales.viewmodel.ManageStoreViewModel;

public class ManageStoreFragment extends Fragment {

    private ManageStoreViewModel mViewModel;

    private EditText mEtStoreName;
    private EditText mEtStoreEmail;
    private EditText mEtStoreAddress;
    private EditText mEtPointsPerPrice;
    private EditText mEtOriginalPassword;
    private EditText mEtNewPassword;
    private Switch mSwChangePassword;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_store, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEtStoreName = getView().findViewById(R.id.etStoreName);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ManageStoreViewModel.class);
        // TODO: Use the ViewModel
    }

}
