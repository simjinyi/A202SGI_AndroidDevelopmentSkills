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
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.UserType;
import com.example.pointofsales.model.state.LoginFormState;
import com.example.pointofsales.utility.LoadingScreen;
import com.example.pointofsales.view.CustomerActivity;
import com.example.pointofsales.view.MainActivity;
import com.example.pointofsales.view.OnSingleClickListener;
import com.example.pointofsales.view.ValidationActivity;
import com.example.pointofsales.view.register.RegisterActivity;
import com.example.pointofsales.viewmodel.LoginViewModel;

/**
 * LoginActivity to handle login
 * Extends ValidationActivity to automatically check for internet availability before performing any operation
 */
public class LoginActivity extends ValidationActivity {

    // Constants for the SharedPreference
    public static final String SP_NAME = "com.example.pointofsales.view.login.SP_NAME";
    public static final String SP_USERNAME = "com.example.pointofsales.view.login.SP_USERNAME";

    // ViewModel
    private LoginViewModel mLoginViewModel;

    // View components
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

        // Set the title for the action bar
        getSupportActionBar().setTitle(R.string.title_activity_login);

        // Get the ViewModel for the activity
        mLoginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        // Check if the user exists in the SharedPreference (Remember Me) and logs the user in automatically
        String emailSaved = null;
        if ((emailSaved = getLoginEmailState()) != null)
            mLoginViewModel.loginFromRemember(emailSaved);

        // Assign the reference to the view components
        mEtEmail = findViewById(R.id.etEmail);
        mEtPassword = findViewById(R.id.etPassword);
        mCbRememberMe = findViewById(R.id.cbRememberMe);
        mBtnLogin = findViewById(R.id.btnLogin);
        mPbLoading = findViewById(R.id.pbLoading);
        mBtnRegister = findViewById(R.id.btnRegister);

        mLoadingScreen = new LoadingScreen(this, mPbLoading);

        // Observe and validate the login form
        mLoginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(LoginFormState loginFormState) {
                if (loginFormState == null)
                    return;

                mBtnLogin.setEnabled(loginFormState.isDataValid());

                // Prompt email error
                if (loginFormState.getEmailError() != null)
                    mEtEmail.setError(getString(loginFormState.getEmailError()));

                // Prompt password error
                if (loginFormState.getPasswordError() != null)
                    mEtPassword.setError(getString(loginFormState.getPasswordError()));
            }
        });

        // Observe the changes on the user details
        mLoginViewModel.getUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {

                // If the user is logged in successfully
                if (user.isLoggedIn()) {

                    // Save the details in the SharedPreference to automatically logs the user into the system next time
                    if (mCbRememberMe.isChecked())
                        persistLoginState(mEtEmail.getText().toString());

                    // Navigate to the appropriate activity based on the type of the user
                    Intent i = null;

                    i = new Intent(LoginActivity.this, user.getType().equals(UserType.CUSTOMER) ? CustomerActivity.class : MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Prevent backpress from coming back to this activity

                    startActivity(i);
                    finish();

                } else if (!(mEtEmail.getText().toString().isEmpty() && mEtPassword.getText().toString().isEmpty())) {

                    // Prompt invalid credentials provided
                    Toast.makeText(LoginActivity.this, getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Form the textwatcher to validate the form details
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

        // Assign the textwatcher to the editTexts in the form for validation
        mEtEmail.addTextChangedListener(afterTextChangedListener);
        mEtPassword.addTextChangedListener(afterTextChangedListener);

        // Login button clicked
        mBtnLogin.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                // Attempt to login with the provided email and password
                mLoginViewModel.login(mEtEmail.getText().toString(), mEtPassword.getText().toString());
            }
        });

        // Register button clicked
        mBtnRegister.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                // Clear the login information provided
                mEtEmail.getText().clear();
                mEtPassword.getText().clear();

                // Navigate to the registration activity
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        // Observe the changes on the loading state of the login form
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

    /**
     * Save the email into the SharedPreference
     * @param email email to be saved
     */
    public void persistLoginState(String email) {
        SharedPreferences sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(SP_USERNAME, email);
        editor.apply();
    }

    /**
     * Get the email saved from the SharedPreference
     * @return email saved, null if no email was saved
     */
    public String getLoginEmailState() {
        SharedPreferences sp = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp.getString(SP_USERNAME, null);
    }
}
