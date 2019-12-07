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

/**
 * LoginViewModel class
 * Implements LoginInterface to callback on login
 */
public class LoginViewModel extends ViewModel implements LoginInterface {

    // MutableLiveData to be observed by the view
    private MutableLiveData<Boolean> mLoading;
    private MutableLiveData<User> mUser;
    private MutableLiveData<LoginFormState> mLoginFormState;

    // Repositories
    private UserRepository mUserRepository;

    public LoginViewModel() {

        // Instantiate the MutableLiveData objects
        mLoginFormState = new MutableLiveData<>();
        mLoginFormState.setValue(new LoginFormState(false));

        mUserRepository = UserRepository.getInstance();
        mUser = mUserRepository.getUser();

        mLoading = new MutableLiveData<>();
        mLoading.setValue(false);
    }

    /**
     * Login from remember (no password needed, just username (email))
     * @param username email to be used to login
     */
    public void loginFromRemember(String username) {
        mLoading.setValue(true);
        mUserRepository.login(username.toLowerCase(), null, this);
    }

    /**
     * Login using username and password
     * @param username email to login
     * @param password password to logi
     */
    public void login(String username, String password) {
        mLoading.setValue(true);
        mUserRepository.login(username.toLowerCase(), password, this);
    }

    /**
     * Validate the login form
     * @param username email to validate
     * @param password password to validate
     */
    public void loginDataChanged(String username, String password) {
        if (!isEmailValid(username)) // Not valid email
            mLoginFormState.setValue(new LoginFormState(R.string.invalid_email, null));
        else if (!isPasswordValid(password)) // Not valid password
            mLoginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        else // Valid data
            mLoginFormState.setValue(new LoginFormState(true));
    }

    /**
     * Check if the email is valid
     * @param username email
     * @return validity of the email
     */
    private boolean isEmailValid(String username) {
        if (username == null)
            return false;

        if (username.trim().isEmpty())
            return false;

        return Patterns.EMAIL_ADDRESS.matcher(username).matches();
    }

    /**
     * Check if the password is longer than 7 characters
     * @param password password
     * @return if the password if longer than 7 characters
     */
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 7;
    }

    // GETTER METHODS
    public LiveData<Boolean> getLoading() { return mLoading; }
    public LiveData<LoginFormState> getLoginFormState() {
        return mLoginFormState;
    }
    public LiveData<User> getUser() {
        return mUser;
    }
    // END GETTER METHODS

    @Override
    public void onLogin(boolean status, DataSnapshot dataSnapshot, String password) {
        if (status) {
            if (dataSnapshot.exists()) {

                dataSnapshot = dataSnapshot.getChildren().iterator().next();

                // Get the user object
                User user = UserDatabase.Converter
                        .mapToUser(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue());

                // If the password is null or the password validated is correct
                if (password == null || PasswordHasher.hash(password, user.getPasswordSalt()).equals(user.getPassword())) {

                    // Set login true
                    user.setLoggedIn(true);
                    mUser.setValue(user);
                }
            }
        }

        // Remove loading flag
        mLoading.setValue(false);
        mUserRepository.notifyObservers();
    }
}
