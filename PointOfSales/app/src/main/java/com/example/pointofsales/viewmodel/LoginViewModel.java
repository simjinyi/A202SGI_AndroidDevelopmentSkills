package com.example.pointofsales.viewmodel;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.R;
import com.example.pointofsales.model.state.LoginFormState;
import com.example.pointofsales.model.User;
import com.example.pointofsales.repository.UserRepository;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<User> mUser;
    private MutableLiveData<LoginFormState> mLoginFormState;
    
    private UserRepository mUserRepository;

    public LoginViewModel() {
        mLoginFormState = new MutableLiveData<>();
        mLoginFormState.setValue(new LoginFormState(false));
        mUserRepository = UserRepository.getInstance();
        mUser = mUserRepository.getUser();
    }

    public void login(String username, String password) {
        mUserRepository.login(username.toLowerCase(), password);
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

    public LiveData<LoginFormState> getLoginFormState() {
        return mLoginFormState;
    }
    public LiveData<User> getUser() {
        return mUser;
    }
}
