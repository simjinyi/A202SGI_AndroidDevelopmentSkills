package com.example.pointofsales.view.account;

import androidx.cardview.widget.CardView;
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
import com.example.pointofsales.viewmodel.StoreAccountViewModel;

public class StoreAccountFragment extends Fragment {

    private StoreAccountViewModel mViewModel;

    private EditText mEtStoreName;
    private EditText mEtStoreEmail;
    protected EditText mEtPassword;
    private EditText mEtStoreAddress;
    private EditText mEtPointsPerPrice;
    protected EditText mEtOriginalPassword;
    protected EditText mEtNewPassword;
    protected Switch mSwChangePassword;
    protected CardView mCvStorePasswordHolder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_store_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEtStoreName = getView().findViewById(R.id.etStoreName);
        mEtStoreEmail = getView().findViewById(R.id.etStoreEmail);
        mEtPassword = getView().findViewById(R.id.etPassword);
        mEtStoreAddress = getView().findViewById(R.id.etStoreAddress);
        mEtPointsPerPrice = getView().findViewById(R.id.etPointsPerPrice);
        mEtOriginalPassword = getView().findViewById(R.id.etOriginalPassword);
        mEtNewPassword = getView().findViewById(R.id.etNewPassword);
        mSwChangePassword = getView().findViewById(R.id.swChangePassword);
        mCvStorePasswordHolder = getView().findViewById(R.id.cvStorePasswordHolder);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(StoreAccountViewModel.class);
    }

}
