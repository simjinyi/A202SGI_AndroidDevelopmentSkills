package com.example.pointofsales.viewmodel;

import android.util.Pair;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.R;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.state.AccountFormEnableState;
import com.example.pointofsales.model.state.StoreAccountFormState;
import com.example.pointofsales.model.state.UserAccountFormState;
import com.example.pointofsales.model.state.UserUpdatedState;
import com.example.pointofsales.repository.PointRepository;
import com.example.pointofsales.repository.UserRepository;
import com.example.pointofsales.utility.PasswordHasher;
import com.example.pointofsales.view.register.RegisterInterface;
import com.google.android.gms.tasks.OnSuccessListener;

public class UserAccountViewModel extends ViewModel implements OnSuccessListener {

    private String mUserId;
    private MutableLiveData<UserAccountFormState> mUserAccountFormState;
    private MutableLiveData<AccountFormEnableState> mAccountFormEnableState;
    private MutableLiveData<Boolean> mUnmatchedPassword;
    private MutableLiveData<UserUpdatedState> mUserUpdated;
    private MutableLiveData<User> mUserData;

    private UserRepository mUserRepository;

    public UserAccountViewModel() {
        mUserRepository = UserRepository.getInstance();

        mUserAccountFormState = new MutableLiveData<>();
        mUserAccountFormState.setValue(new UserAccountFormState(false));

        mAccountFormEnableState = new MutableLiveData<>();
        mAccountFormEnableState.setValue(new AccountFormEnableState(true, true, true, false, false, false, false));

        mUnmatchedPassword = new MutableLiveData<>();
        mUnmatchedPassword.setValue(false);

        mUserUpdated = new MutableLiveData<>();
        mUserUpdated.setValue(UserUpdatedState.NONE);
    }

    public UserAccountViewModel(String userId) {
        mUserRepository = UserRepository.getInstance();

        mUserId = UserViewModel.getUserId();

        mUserAccountFormState = new MutableLiveData<>();
        mUserAccountFormState.setValue(new StoreAccountFormState(false));

        mAccountFormEnableState = new MutableLiveData<>();
        mAccountFormEnableState.setValue(new AccountFormEnableState(true, false, false, false, false, true, false));

        mUnmatchedPassword = new MutableLiveData<>();
        mUnmatchedPassword.setValue(false);

        mUserUpdated = new MutableLiveData<>();
        mUserUpdated.setValue(UserUpdatedState.NONE);

        mUserData = UserRepository.getInstance().getUser();
    }

    public void updateUser(Pair<User, String> pair) {

        User oriUser = mUserRepository.getUserValue();
        User user = pair.first;
        user.setId(oriUser.getId());

        if (!mAccountFormEnableState.getValue().isChangePasswordEnabled()) {

            user.setPasswordSalt(oriUser.getPasswordSalt());
            user.setPassword(oriUser.getPassword());
            mUserRepository.update(user, this);

        } else {

            String hashedPassword = PasswordHasher.hash(pair.second, oriUser.getPasswordSalt());

            if (!hashedPassword.equals(oriUser.getPassword())) {
                mUnmatchedPassword.setValue(true);
            } else {

                user.setPasswordSalt(PasswordHasher.generateRandomSalt());
                user.setPassword(PasswordHasher.hash(user.getPassword(), user.getPasswordSalt()));

                mUserRepository.update(user, this);
            }
        }

        if (!oriUser.getName().equals(user.getName()))
            PointRepository.updateUserName(user.getId(), user.getName());
    }

    public void insertUser(final User user) {

        user.setEmail(user.getEmail().toLowerCase());
        user.setPasswordSalt(PasswordHasher.generateRandomSalt());
        user.setPassword(PasswordHasher.hash(user.getPassword(), user.getPasswordSalt()));

        mUserRepository.get(user.getEmail(), new RegisterInterface() {
            @Override
            public void isUsernameValid(boolean isValid) {
                if (isValid)
                    mUserRepository.insert(user, UserAccountViewModel.this);
                else
                    mUserUpdated.setValue(UserUpdatedState.FAILED);
            }
        });
    }

    public void userAccountFormChanged(String name, String email, String password, String originalPassword, String newPassword) {

        if (name.trim().isEmpty()) {
            mUserAccountFormState.setValue(new StoreAccountFormState(R.string.invalid_name, null, null, null, null, null, null));
            return;
        }

        if (mUserId == null) {
            if (!isEmailValid(email)) {
                mUserAccountFormState.setValue(new StoreAccountFormState(null, R.string.invalid_email, null, null, null, null, null));
                return;
            }

            if (!isPasswordValid(password)) {
                mUserAccountFormState.setValue(new StoreAccountFormState(null, null, R.string.invalid_password, null, null, null, null));
                return;
            }
        } else if (mAccountFormEnableState.getValue().isChangePasswordEnabled()) {
            if (!isPasswordValid(originalPassword)) {
                mUserAccountFormState.setValue(new StoreAccountFormState(null, null, null, R.string.invalid_password, null, null, null));
                return;
            }

            if (!isPasswordValid(newPassword)) {
                mUserAccountFormState.setValue(new StoreAccountFormState(null, null, null, null, R.string.invalid_password, null, null));
                return;
            }
        }

        mUserAccountFormState.setValue(new StoreAccountFormState(true));
    }

    public void setEnableChangePassword(boolean changePasswordEnabled) {
        if (mUserId != null && changePasswordEnabled)
            mAccountFormEnableState.setValue(new AccountFormEnableState(true, false, false, false, false, true, true));
        else
            mAccountFormEnableState.setValue(new AccountFormEnableState(true, false, false, false, false, true, false));
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

    public LiveData<UserAccountFormState> getUserAccountFormState() {
        return mUserAccountFormState;
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
