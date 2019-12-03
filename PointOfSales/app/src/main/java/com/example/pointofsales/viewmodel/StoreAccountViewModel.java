package com.example.pointofsales.viewmodel;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.R;
import com.example.pointofsales.model.state.AccountFormEnableState;
import com.example.pointofsales.model.state.StoreAccountFormState;

public class StoreAccountViewModel extends ViewModel {

    private String mStoreId;
    private MutableLiveData<StoreAccountFormState> mStoreAccountFormState;
    private MutableLiveData<AccountFormEnableState> mAccountFormEnableState;

    public StoreAccountViewModel() {
        mStoreAccountFormState = new MutableLiveData<>();
        mStoreAccountFormState.setValue(new StoreAccountFormState(false));

        mAccountFormEnableState = new MutableLiveData<>();
        mAccountFormEnableState.setValue(new AccountFormEnableState(true, true, true, true, false, false));
    }

    public StoreAccountViewModel(String storeId) {
        mStoreId = storeId;
        mStoreAccountFormState = new MutableLiveData<>();
        mStoreAccountFormState.setValue(new StoreAccountFormState(false));

        mAccountFormEnableState = new MutableLiveData<>();
        mAccountFormEnableState.setValue(new AccountFormEnableState(false, false, true, true, true, false));
    }

    public void storeAccountFormChanged(String name, String email, String password, String originalPassword, String newPassword, String address, String pointsPerPrice) {

        if (name.trim().isEmpty()) {
            mStoreAccountFormState.setValue(new StoreAccountFormState(R.string.invalid_name, null, null, null, null, null, null));
            return;
        }

        if (mStoreId == null) {
            if (!isEmailValid(email)) {
                mStoreAccountFormState.setValue(new StoreAccountFormState(null, R.string.invalid_email, null, null, null, null, null));
                return;
            }

            if (!isPasswordValid(password)) {
                mStoreAccountFormState.setValue(new StoreAccountFormState(null, null, R.string.invalid_password, null, null, null, null));
                return;
            }
        } else if (mAccountFormEnableState.getValue().isChangePasswordEnabled()) {
            if (!isPasswordValid(originalPassword)) {
                mStoreAccountFormState.setValue(new StoreAccountFormState(null, null, null, R.string.invalid_password, null, null, null));
                return;
            }

            if (!isPasswordValid(newPassword)) {
                mStoreAccountFormState.setValue(new StoreAccountFormState(null, null, null, null, R.string.invalid_password, null, null));
                return;
            }
        }

        int pointsPerPriceParsed = 0;
        boolean isPointsPerPriceValid = true;

        try {
            pointsPerPriceParsed = Integer.parseInt(pointsPerPrice);

            if (pointsPerPriceParsed <= 0)
                isPointsPerPriceValid = false;
        } catch (Exception e) {
            isPointsPerPriceValid = false;
        }

        if (address.trim().isEmpty()) {
            mStoreAccountFormState.setValue(new StoreAccountFormState(null, null, null, null, null, R.string.invalid_address, null));
            return;
        }

        if (!isPointsPerPriceValid) {
            mStoreAccountFormState.setValue(new StoreAccountFormState(null, null, null, null, null, null, R.string.invalid_points_per_price));
            return;
        }

        mStoreAccountFormState.setValue(new StoreAccountFormState(true));
    }

    public void setEnableChangePassword(boolean changePasswordEnabled) {
        if (mStoreId != null && changePasswordEnabled)
            mAccountFormEnableState.setValue(new AccountFormEnableState(false, false, true, true, true, true));
        else
            mAccountFormEnableState.setValue(new AccountFormEnableState(false, false, true, true, true, false));
    }

    private boolean isEmailValid(String username) {
        if (username == null)
            return false;

        if (username.contains("@"))
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();

        return !username.trim().isEmpty();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 7;
    }

    public LiveData<StoreAccountFormState> getStoreAccountFormState() {
        return mStoreAccountFormState;
    }
    public LiveData<AccountFormEnableState> getAccountFormEnableState() {
        return mAccountFormEnableState;
    }
}
