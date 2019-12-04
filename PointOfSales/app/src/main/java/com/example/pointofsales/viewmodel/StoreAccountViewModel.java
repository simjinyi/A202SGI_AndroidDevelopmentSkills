package com.example.pointofsales.viewmodel;

import android.util.Pair;
import android.util.Patterns;
import android.widget.Button;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.model.Store;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.state.AccountFormEnableState;
import com.example.pointofsales.model.state.StoreAccountFormState;
import com.example.pointofsales.model.state.UserUpdatedState;
import com.example.pointofsales.repository.UserRepository;
import com.example.pointofsales.view.register.RegisterInterface;
import com.google.android.gms.tasks.OnSuccessListener;

public class StoreAccountViewModel extends ViewModel implements OnSuccessListener {

    private String mStoreId;
    private MutableLiveData<StoreAccountFormState> mStoreAccountFormState;
    private MutableLiveData<AccountFormEnableState> mAccountFormEnableState;
    private MutableLiveData<Boolean> mUnmatchedPassword;
    private MutableLiveData<UserUpdatedState> mUserUpdated;
    private MutableLiveData<User> mUserData;

    private UserRepository mUserRepository;

    public StoreAccountViewModel() {
        mUserRepository = UserRepository.getInstance();

        mStoreAccountFormState = new MutableLiveData<>();
        mStoreAccountFormState.setValue(new StoreAccountFormState(false));

        mAccountFormEnableState = new MutableLiveData<>();
        mAccountFormEnableState.setValue(new AccountFormEnableState(true, true, true, true, false, false));

        mUnmatchedPassword = new MutableLiveData<>();
        mUnmatchedPassword.setValue(false);

        mUserUpdated = new MutableLiveData<>();
        mUserUpdated.setValue(UserUpdatedState.NONE);
    }

    public StoreAccountViewModel(String storeId) {
        mUserRepository = UserRepository.getInstance();

        mStoreId = storeId;

        mStoreAccountFormState = new MutableLiveData<>();
        mStoreAccountFormState.setValue(new StoreAccountFormState(false));

        mAccountFormEnableState = new MutableLiveData<>();
        mAccountFormEnableState.setValue(new AccountFormEnableState(false, false, true, true, true, false));

        mUnmatchedPassword = new MutableLiveData<>();
        mUnmatchedPassword.setValue(false);

        mUserUpdated = new MutableLiveData<>();
        mUserUpdated.setValue(UserUpdatedState.NONE);

        mUserData = UserRepository.getInstance().getUser();
    }

    public void updateStore(Pair<Store, String> pair) {

        Store oriStore = (Store) mUserRepository.getUserValue();
        Store store = pair.first;
        store.setId(oriStore.getId());

        if (!mAccountFormEnableState.getValue().isChangePasswordEnabled()) {
            store.setPassword(oriStore.getPassword());
            mUserRepository.update(store, this);
        } else {
            if (!pair.second.equals(oriStore.getPassword())) {
                mUnmatchedPassword.setValue(true);
            } else {

                mUserRepository.update(store, this);
            }
        }
    }

    public void insertStore(final Store store) {
        store.setEmail(store.getEmail().toLowerCase());
        mUserRepository.get(store.getEmail(), new RegisterInterface() {
            @Override
            public void isUsernameValid(boolean isValid) {
                if (isValid)
                    mUserRepository.insert(store, StoreAccountViewModel.this);
                else
                    mUserUpdated.setValue(UserUpdatedState.FAILED);
            }
        });
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

        if (username.trim().isEmpty())
            return false;

        return Patterns.EMAIL_ADDRESS.matcher(username).matches();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 7;
    }

    public void clearUnmatchedPasswordFlag() {
        mUnmatchedPassword.setValue(false);
    }

    public void clearUserUpdatedFlag() {
        mUserUpdated.setValue(UserUpdatedState.NONE);
    }

    public LiveData<StoreAccountFormState> getStoreAccountFormState() {
        return mStoreAccountFormState;
    }
    public LiveData<AccountFormEnableState> getAccountFormEnableState() {
        return mAccountFormEnableState;
    }
    public LiveData<Boolean> getUnmatchedPassword() {
        return mUnmatchedPassword;
    }
    public LiveData<UserUpdatedState> getUserUpdated() {
        return mUserUpdated;
    }
    public LiveData<User> getUserData() {
        return mUserData;
    }

    @Override
    public void onSuccess(Object o) {
        if (o instanceof String)
            mUserUpdated.setValue(UserUpdatedState.SUCCESS);
    }
}