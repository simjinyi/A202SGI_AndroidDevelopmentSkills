package com.example.pointofsales.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pointofsales.view.login.LoginActivity;
import com.example.pointofsales.viewmodel.LoginViewModel;

public abstract class UserValidationActivity extends AppCompatActivity {

    private String mStoreId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (true) {
//        if (LoginViewModel.isLoggedIn()) {
//            mStoreId = LoginViewModel.getUserId();
            mStoreId = "0";
            onCreateValidated(savedInstanceState);
        } else {
            startActivity(new Intent(UserValidationActivity.this, LoginActivity.class));
            finish();
        }
    }

    protected abstract void onCreateValidated(@Nullable Bundle savedInstanceState);

    public String getStoreId() {
        return mStoreId;
    }
}
