package com.example.pointofsales.viewmodel;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.R;
import com.example.pointofsales.model.validation.LoginFormState;
import com.example.pointofsales.model.User;
import com.example.pointofsales.repository.UserRepository;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<User> mUser;
    private MutableLiveData<LoginFormState> mLoginFormState;

    public LoginViewModel() {
        mLoginFormState = new MutableLiveData<>();
        mLoginFormState.setValue(new LoginFormState(true));
        mUser = UserRepository.getInstance()
                .getUser();
    }

    public void login(String username, String password) {
        UserRepository.getInstance()
                .login(username, password);
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username))
            mLoginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        else if (!isPasswordValid(password))
            mLoginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        else
            mLoginFormState.setValue(new LoginFormState(true));
    }

    private boolean isUserNameValid(String username) {
        if (username == null)
            return false;

        if (username.contains("@"))
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();

        return !username.trim().isEmpty();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    public static boolean isLoggedIn() {
        return UserRepository.getInstance().getUser().getValue().isLoggedIn();
    }

    public static String getUserId() {
        return UserRepository.getInstance().getUser().getValue().getId();
    }

    public LiveData<LoginFormState> getLoginFormState() {
        return mLoginFormState;
    }
    public LiveData<User> getUser() {
        return mUser;
    }
}
