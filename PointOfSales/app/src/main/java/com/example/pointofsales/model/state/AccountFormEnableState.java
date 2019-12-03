package com.example.pointofsales.model.state;

public class AccountFormEnableState {

    private boolean mIsEmailEnabled;
    private boolean mIsPasswordVisible;
    private boolean mIsAddressVisible;
    private boolean mIsPointsPerPriceVisible;
    private boolean mIsChangePasswordVisible;
    private boolean mIsChangePasswordEnabled;

    public AccountFormEnableState(boolean isEmailEnabled, boolean isPasswordVisible, boolean isAddressVisible, boolean isPointsPerPriceVisible, boolean isChangePasswordVisible, boolean isChangePasswordEnabled) {
        mIsEmailEnabled = isEmailEnabled;
        mIsPasswordVisible = isPasswordVisible;
        mIsAddressVisible = isAddressVisible;
        mIsPointsPerPriceVisible = isPointsPerPriceVisible;
        mIsChangePasswordVisible = isChangePasswordVisible;
        mIsChangePasswordEnabled = isChangePasswordEnabled;
    }

    public boolean isEmailEnabled() {
        return mIsEmailEnabled;
    }

    public boolean isPasswordVisible() {
        return mIsPasswordVisible;
    }

    public boolean isAddressVisible() {
        return mIsAddressVisible;
    }

    public boolean isPointsPerPriceVisible() {
        return mIsPointsPerPriceVisible;
    }

    public boolean isChangePasswordVisible() {
        return mIsChangePasswordVisible;
    }

    public boolean isChangePasswordEnabled() {
        return mIsChangePasswordEnabled;
    }
}
