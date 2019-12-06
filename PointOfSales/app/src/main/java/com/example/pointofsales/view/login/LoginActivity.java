package com.example.pointofsales.view.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.pointofsales.R;
import com.example.pointofsales.utility.LoadingScreen;
import com.example.pointofsales.model.state.LoginFormState;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.UserType;
import com.example.pointofsales.view.CustomerActivity;
import com.example.pointofsales.view.MainActivity;
import com.example.pointofsales.view.OnSingleClickListener;
import com.example.pointofsales.view.ValidationActivity;
import com.example.pointofsales.view.register.RegisterActivity;
import com.example.pointofsales.viewmodel.LoginViewModel;

public class LoginActivity extends ValidationActivity {

    public static final String SP_NAME = "com.example.pointofsales.view.login.SP_NAME";
    public static final String SP_USERNAME = "com.example.pointofsales.view.login.SP_USERNAME";

    private LoginViewModel mLoginViewModel;

    private EditText mEtEmail;
    private EditText mEtPassword;
    private CheckBox mCbRememberMe;
    private Button mBtnLogin;
    private ProgressBar mPbLoading;
    private Button mBtnRegister;

    private LoadingScreen mLoadingScreen;

    @Override
    public void onCreateValidated(Bundle savedInstanceState) {
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle(R.string.title_activity_login);
        mLoginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        String emailSaved = null;
        if ((emailSaved = getLoginEmailState()) != null)
            mLoginViewModel.loginFromRemember(emailSaved);

        mEtEmail = findViewById(R.id.etEmail);
        mEtPassword = findViewById(R.id.etPassword);
        mCbRememberMe = findViewById(R.id.cbRememberMe);
        mBtnLogin = findViewById(R.id.btnLogin);
        mPbLoading = findViewById(R.id.pbLoading);
        mBtnRegister = findViewById(R.id.btnRegister);

        mLoadingScreen = new LoadingScreen(this, mPbLoading);

        mLoginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(LoginFormState loginFormState) {
                if (loginFormState == null)
                    return;

                mBtnLogin.setEnabled(loginFormState.isDataValid());

                if (loginFormState.getEmailError() != null)
                    mEtEmail.setError(getString(loginFormState.getEmailError()));

                if (loginFormState.getPasswordError() != null)
                    mEtPassword.setError(getString(loginFormState.getPasswordError()));
            }
        });

        mLoginViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user.isLoggedIn()) {

                    if (mCbRememberMe.isChecked())
                        persistLoginState(mEtEmail.getText().toString());

                    Intent i = null;
                    i = new Intent(LoginActivity.this, user.getType().equals(UserType.CUSTOMER) ? CustomerActivity.class : MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();

                } else if (!(mEtEmail.getText().toString().isEmpty() && mEtPassword.getText().toString().isEmpty())) {
                    Toast.makeText(LoginActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                mLoginViewModel.loginDataChanged(mEtEmail.getText().toString(), mEtPassword.getText().toString());
            }
        };

        mEtEmail.addTextChangedListener(afterTextChangedListener);
        mEtPassword.addTextChangedListener(afterTextChangedListener);

        mBtnLogin.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mLoginViewModel.login(mEtEmail.getText().toString(), mEtPassword.getText().toString());
            }
        });

        mBtnRegister.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mEtEmail.getText().clear();
                mEtPassword.getText().clear();
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        mLoginViewModel.getLoading().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                    mLoadingScreen.start();
                else
                    mLoadingScreen.end();
            }
        });
    }

    public void persistLoginState(String email) {
        SharedPreferences sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SP_USERNAME, email);
        editor.apply();
    }

    public String getLoginEmailState() {
        SharedPreferences sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(SP_USERNAME, null);
    }
}
