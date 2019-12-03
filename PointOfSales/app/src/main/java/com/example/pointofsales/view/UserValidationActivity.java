package com.example.pointofsales.view;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.pointofsales.model.Store;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.UserType;
import com.example.pointofsales.repository.UserRepository;
import com.example.pointofsales.view.login.LoginActivity;
import com.example.pointofsales.viewmodel.LoginViewModel;
import com.example.pointofsales.viewmodel.UserViewModel;

public abstract class UserValidationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (UserViewModel.isLoggedIn()) {
            onCreateValidated(savedInstanceState);
        } else {
            Intent i = new Intent(UserValidationActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

    protected abstract void onCreateValidated(@Nullable Bundle savedInstanceState);
}
