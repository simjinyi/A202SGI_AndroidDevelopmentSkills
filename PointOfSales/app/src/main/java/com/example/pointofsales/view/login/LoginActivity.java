package com.example.pointofsales.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.pointofsales.R;
import com.example.pointofsales.helper.LoadingScreenHelper;
import com.example.pointofsales.model.validation.LoginFormState;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.UserType;
import com.example.pointofsales.view.MainActivity;
import com.example.pointofsales.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel mLoginViewModel;

    private EditText mEtEmail;
    private EditText mEtPassword;
    private CheckBox mCbRememberMe;
    private Button mBtnLogin;
    private ProgressBar mPbLoading;
    private Button mBtnRegister;

    private LoadingScreenHelper mLoadingScreenHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        mEtEmail = findViewById(R.id.etEmail);
        mEtPassword = findViewById(R.id.etPassword);
        mCbRememberMe = findViewById(R.id.cbRememberMe);
        mBtnLogin = findViewById(R.id.btnLogin);
        mPbLoading = findViewById(R.id.pbLoading);
        mBtnRegister = findViewById(R.id.btnRegister);

        mLoadingScreenHelper = new LoadingScreenHelper(this, mPbLoading);

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
                if (!(mEtEmail.getText().toString().isEmpty() && mEtPassword.getText().toString().isEmpty())) {
                    if (user.isLoggedIn()) {
                        if (user.getType().equals(UserType.CUSTOMER)) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        } else {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            Toast.makeText(LoginActivity.this, getString(R.string.welcome) + user.getName(), Toast.LENGTH_SHORT).show();
                        }
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                    }
                }

                mLoadingScreenHelper.end();
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

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoginViewModel.login(mEtEmail.getText().toString(), mEtPassword.getText().toString());
                mLoadingScreenHelper.start();
            }
        });

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
