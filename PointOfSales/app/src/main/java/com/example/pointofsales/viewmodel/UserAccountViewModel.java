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

/**
 * UserAccountViewModel to validate the User Account Form
 * Implements OnSuccessListener to callback setting the user updated state on successful operation
 */
public class UserAccountViewModel extends ViewModel implements OnSuccessListener {

    private String mUserId;

    // MutableLiveData observed by the views
    private MutableLiveData<UserAccountFormState> mUserAccountFormState;
    private MutableLiveData<AccountFormEnableState> mAccountFormEnableState;
    private MutableLiveData<Boolean> mUnmatchedPassword;
    private MutableLiveData<UserUpdatedState> mUserUpdated;
    private MutableLiveData<User> mUserData;

    // Repositories
    private UserRepository mUserRepository;

    /**
     * Constructor for non logged in customer
     */
    public UserAccountViewModel() {
        mUserRepository = UserRepository.getInstance();

        // Instantiate the MutableLiveData
        mUserAccountFormState = new MutableLiveData<>();
        mUserAccountFormState.setValue(new UserAccountFormState(false));

        mAccountFormEnableState = new MutableLiveData<>();
        mAccountFormEnableState.setValue(new AccountFormEnableState(true, true, true, false, false, false, false));

        mUnmatchedPassword = new MutableLiveData<>();
        mUnmatchedPassword.setValue(false);

        mUserUpdated = new MutableLiveData<>();
        mUserUpdated.setValue(UserUpdatedState.NONE);
    }

    /**
     * Constructor for logged in customer
     */
    public UserAccountViewModel(String userId) {
        mUserRepository = UserRepository.getInstance();

        mUserId = UserViewModel.getUserId();

        // Instantiate the MutableLiveData
        mUserAccountFormState = new MutableLiveData<>();
        mUserAccountFormState.setValue(new StoreAccountFormState(false));

        mAccountFormEnableState = new MutableLiveData<>();
        mAccountFormEnableState.setValue(new AccountFormEnableState(true, false, false, false, false, true, false));

        mUnmatchedPassword = new MutableLiveData<>();
        mUnmatchedPassword.setValue(false);

        mUserUpdated = new MutableLiveData<>();
        mUserUpdated.setValue(UserUpdatedState.NONE);

        // Get the user data
        mUserData = UserRepository.getInstance().getUser();
    }

    /**
     * Update the customer details
     * @param pair updated User object and the original password for validation
     */
    public void updateUser(Pair<User, String> pair) {

        User oriUser = mUserRepository.getUserValue();
        User user = pair.first;
        user.setId(oriUser.getId());

        // If the change password was not enabled
        if (!mAccountFormEnableState.getValue().isChangePasswordEnabled()) {

            // Update the customer details without changing the password
            user.setPasswordSalt(oriUser.getPasswordSalt());
            user.setPassword(oriUser.getPassword());
            mUserRepository.update(user, this);

        } else {

            // Hash the password
            String hashedPassword = PasswordHasher.hash(pair.second, oriUser.getPasswordSalt());

            // Check if the original password entered by the user matches the original store password
            if (!hashedPassword.equals(oriUser.getPassword())) {
                mUnmatchedPassword.setValue(true);
            } else {

                // Update the store details with and change the password in the database
                user.setPasswordSalt(PasswordHasher.generateRandomSalt());
                user.setPassword(PasswordHasher.hash(user.getPassword(), user.getPasswordSalt()));

                mUserRepository.update(user, this);
            }
        }

        // If the name was changed, update to the Point (Membership) repository and database
        if (!oriUser.getName().equals(user.getName()))
            PointRepository.updateUserName(user.getId(), user.getName());
    }

    /**
     * Insert the user into the database
     * @param user user to be added
     */
    public void insertUser(final User user) {

        user.setEmail(user.getEmail().toLowerCase());

        // Hash the password
        user.setPasswordSalt(PasswordHasher.generateRandomSalt());
        user.setPassword(PasswordHasher.hash(user.getPassword(), user.getPasswordSalt()));

        mUserRepository.get(user.getEmail(), new RegisterInterface() {
            @Override
            public void isUsernameValid(boolean isValid) {

                // If the username (email) is valid, insert the customer, otherwise prompt error
                if (isValid)
                    mUserRepository.insert(user, UserAccountViewModel.this);
                else
                    mUserUpdated.setValue(UserUpdatedState.FAILED);
            }
        });
    }

    /**
     * Validate the user account form
     * @param name name to be validated
     * @param email email to be validated
     * @param password password to be validated
     * @param originalPassword original password to be validated
     * @param newPassword new password to be validated
     */
    public void userAccountFormChanged(String name, String email, String password, String originalPassword, String newPassword) {

        // Name cannot be empty
        if (name.trim().isEmpty()) {
            mUserAccountFormState.setValue(new StoreAccountFormState(R.string.invalid_name, null, null, null, null, null, null));
            return;
        }

        // Registration
        if (mUserId == null) {

            // Invalid email
            if (!isEmailValid(email)) {
                mUserAccountFormState.setValue(new StoreAccountFormState(null, R.string.invalid_email, null, null, null, null, null));
                return;
            }

            // Invalid password
            if (!isPasswordValid(password)) {
                mUserAccountFormState.setValue(new StoreAccountFormState(null, null, R.string.invalid_password, null, null, null, null));
                return;
            }
        } else if (mAccountFormEnableState.getValue().isChangePasswordEnabled()) {

            // Invalid original password
            if (!isPasswordValid(originalPassword)) {
                mUserAccountFormState.setValue(new StoreAccountFormState(null, null, null, R.string.invalid_password, null, null, null));
                return;
            }

            // Invalid change password
            if (!isPasswordValid(newPassword)) {
                mUserAccountFormState.setValue(new StoreAccountFormState(null, null, null, null, R.string.invalid_password, null, null));
                return;
            }
        }

        // Everything is valid
        mUserAccountFormState.setValue(new StoreAccountFormState(true));
    }

    /**
     * Set the account form enable state when the check password switch is enabled
     * @param changePasswordEnabled password switch is enabled
     */
    public void setEnableChangePassword(boolean changePasswordEnabled) {
        if (mUserId != null && changePasswordEnabled)
            mAccountFormEnableState.setValue(new AccountFormEnableState(true, false, false, false, false, true, true));
        else
            mAccountFormEnableState.setValue(new AccountFormEnableState(true, false, false, false, false, true, false));
    }

    /**
     * Check if the given email is valid
     * @param username email to be validated
     * @return if the email is valid
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
     * @param password password to be validated
     * @return if the password is longer than 7 characters
     */
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 7;
    }

    // Flags clearer
    public void clearUnmatchedPasswordFlag() {
        mUnmatchedPassword.setValue(false);
    }

    public void clearUserUpdatedFlag() {
        mUserUpdated.setValue(UserUpdatedState.NONE);
    }
    // END Flags clearer

    // GETTER METHODS
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
    // END GETTER METHODS

    /**
     * On success callback, if a String was returned from the repository, the user details was updated successfully
     * @param o Object returned
     */
    @Override
    public void onSuccess(Object o) {
        if (o instanceof String)
            mUserUpdated.setValue(UserUpdatedState.SUCCESS);
    }
}
