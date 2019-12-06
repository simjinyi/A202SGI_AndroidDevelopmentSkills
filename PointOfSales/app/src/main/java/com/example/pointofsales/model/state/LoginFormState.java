package com.example.pointofsales.model.state;

/**
 * Model class to store the login form fields validation
 */
public class LoginFormState {

    private Integer mEmailError;
    private Integer mPasswordError;
    private boolean mIsDataValid;

    public LoginFormState(Integer emailError, Integer passwordError) {
        mEmailError = emailError;
        mPasswordError = passwordError;
        mIsDataValid = false;
    }

    public LoginFormState(boolean isDataValid) {
        mEmailError = null;
        mPasswordError = null;
        mIsDataValid = isDataValid;
    }

    public Integer getEmailError() {
        return mEmailError;
    }

    public Integer getPasswordError() {
        return mPasswordError;
    }

    public boolean isDataValid() {
        return mIsDataValid;
    }
}
