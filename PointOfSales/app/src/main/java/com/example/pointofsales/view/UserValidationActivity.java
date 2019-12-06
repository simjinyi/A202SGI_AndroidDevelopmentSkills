package com.example.pointofsales.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;

import com.example.pointofsales.view.login.LoginActivity;
import com.example.pointofsales.viewmodel.UserViewModel;

public abstract class UserValidationActivity extends ValidationActivity {

    protected void onCreateValidated(@Nullable Bundle savedInstanceState) {
        if (UserViewModel.isLoggedIn()) {
            onCreateLoginValidated(savedInstanceState);
        } else {
            Intent i = new Intent(UserValidationActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
    }

    protected abstract void onCreateLoginValidated(@Nullable Bundle savedInstanceState);

    public void invalidateLoginState() {
        SharedPreferences sp = getSharedPreferences(LoginActivity.SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().apply();
        editor.commit();
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getSupportFragmentManager().popBackStack();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
