package com.example.pointofsales.viewmodel;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.R;
import com.example.pointofsales.database.UserDatabase;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.state.LoginFormState;
import com.example.pointofsales.repository.UserRepository;
import com.example.pointofsales.utility.PasswordHasher;
import com.example.pointofsales.view.login.LoginInterface;
import com.google.firebase.database.DataSnapshot;

import java.util.Map;

public class LoginViewModel extends ViewModel implements LoginInterface {

    private MutableLiveData<Boolean> mLoading;
    private MutableLiveData<User> mUser;
    private MutableLiveData<LoginFormState> mLoginFormState;
    
    private UserRepository mUserRepository;

    public LoginViewModel() {

        mLoginFormState = new MutableLiveData<>();
        mLoginFormState.setValue(new LoginFormState(false));

        mUserRepository = UserRepository.getInstance();
        mUser = mUserRepository.getUser();

        mLoading = new MutableLiveData<>();
        mLoading.setValue(false);
    }

    public void loginFromRemember(String username) {
        mLoading.setValue(true);
        mUserRepository.login(username.toLowerCase(), null, this);
    }

    public void login(String username, String password) {
        mLoading.setValue(true);
        mUserRepository.login(username.toLowerCase(), password, this);
    }

    public void loginDataChanged(String username, String password) {
        if (!isEmailValid(username))
            mLoginFormState.setValue(new LoginFormState(R.string.invalid_email, null));
        else if (!isPasswordValid(password))
            mLoginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        else
            mLoginFormState.setValue(new LoginFormState(true));
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

    public LiveData<Boolean> getLoading() { return mLoading; }
    public LiveData<LoginFormState> getLoginFormState() {
        return mLoginFormState;
    }
    public LiveData<User> getUser() {
        return mUser;
    }

    @Override
    public void onLogin(boolean status, DataSnapshot dataSnapshot, String password) {
        if (status) {
            if (dataSnapshot.exists()) {

                dataSnapshot = dataSnapshot.getChildren().iterator().next();

                User user = UserDatabase.Converter
                        .mapToUser(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue());

                if (password == null || PasswordHasher.hash(password, user.getPasswordSalt()).equals(user.getPassword())) {
                    user.setLoggedIn(true);
                    mUser.setValue(user);
                }
            }
        }

        mLoading.setValue(false);
        mUserRepository.notifyObservers();
    }
}
