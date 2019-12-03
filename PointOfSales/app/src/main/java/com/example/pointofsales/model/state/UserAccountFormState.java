package com.example.pointofsales.model.state;

public class UserAccountFormState {

    private Integer mNameError;
    private Integer mEmailError;
    private Integer mPasswordError;
    private Integer mOriginalPasswordError;
    private Integer mNewPasswordError;

    private boolean mIsDataValid;

    public UserAccountFormState(Integer nameError, Integer emailError, Integer passwordError, Integer originalPasswordErrorInteger, Integer newPasswordError) {
        mNameError = nameError;
        mEmailError = emailError;
        mPasswordError = passwordError;
        mOriginalPasswordError = originalPasswordErrorInteger;
        mNewPasswordError = newPasswordError;
        mIsDataValid = false;
    }

    public UserAccountFormState(boolean isDataValid) {
        mNameError = null;
        mEmailError = null;
        mPasswordError = null;
        mOriginalPasswordError = null;
        mNewPasswordError = null;
        mIsDataValid = isDataValid;
    }

    public Integer getNameError() {
        return mNameError;
    }

    public Integer getEmailError() {
        return mEmailError;
    }

    public Integer getPasswordError() {
        return mPasswordError;
    }

    public Integer getOriginalPasswordError() {
        return mOriginalPasswordError;
    }

    public Integer getNewPasswordError() {
        return mNewPasswordError;
    }

    public boolean isDataValid() {
        return mIsDataValid;
    }
}
