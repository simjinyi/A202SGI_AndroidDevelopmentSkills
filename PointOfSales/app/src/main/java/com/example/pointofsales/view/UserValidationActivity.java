package com.example.pointofsales.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pointofsales.view.login.LoginActivity;
import com.example.pointofsales.viewmodel.LoginViewModel;

public abstract class UserValidationActivity extends AppCompatActivity {

    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LoginViewModel.isLoggedIn()) {
            userId = LoginViewModel.getUserId();
        } else {
            startActivity(new Intent(UserValidationActivity.this, LoginActivity.class));
            finish();
        }
    }

    public String getUserId() {
        return userId;
    }
}
