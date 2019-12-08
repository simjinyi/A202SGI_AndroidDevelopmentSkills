package com.example.pointofsales.viewmodel;

import android.util.Pair;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Store;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.state.AccountFormEnableState;
import com.example.pointofsales.model.state.StoreAccountFormState;
import com.example.pointofsales.model.state.UserUpdatedState;
import com.example.pointofsales.repository.PointRepository;
import com.example.pointofsales.repository.TransactionRepository;
import com.example.pointofsales.repository.UserRepository;
import com.example.pointofsales.utility.PasswordHasher;
import com.example.pointofsales.view.register.RegisterInterface;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * StoreAccountViewModel to validate the Store Account Form
 * Implements OnSuccessListener to callback setting the user updated state on successful operation
 */
public class StoreAccountViewModel extends ViewModel implements OnSuccessListener {

    private String mStoreId;

    // MutableLiveData observed by the views
    private MutableLiveData<StoreAccountFormState> mStoreAccountFormState;
    private MutableLiveData<AccountFormEnableState> mAccountFormEnableState;
    private MutableLiveData<Boolean> mUnmatchedPassword;
    private MutableLiveData<UserUpdatedState> mUserUpdated;
    private MutableLiveData<User> mUserData;

    // Repositories
    private UserRepository mUserRepository;
    private CheckoutViewModel mCheckOutViewModel;

    /**
     * Constructor for non logged in seller
     */
    public StoreAccountViewModel() {
        mUserRepository = UserRepository.getInstance();

        // Instantiate the MutableLiveData
        mStoreAccountFormState = new MutableLiveData<>();
        mStoreAccountFormState.setValue(new StoreAccountFormState(false));

        mAccountFormEnableState = new MutableLiveData<>();
        mAccountFormEnableState.setValue(new AccountFormEnableState(true, true, true, true, true, false, false));

        mUnmatchedPassword = new MutableLiveData<>();
        mUnmatchedPassword.setValue(false);

        mUserUpdated = new MutableLiveData<>();
        mUserUpdated.setValue(UserUpdatedState.NONE);
    }

    /**
     * Constructor for logged in seller
     */
    public StoreAccountViewModel(CheckoutViewModel checkoutViewModel) {
        mUserRepository = UserRepository.getInstance();
        mCheckOutViewModel = checkoutViewModel;

        mStoreId = UserViewModel.getUserId();

        // Instantiate the MutableLiveData
        mStoreAccountFormState = new MutableLiveData<>();
        mStoreAccountFormState.setValue(new StoreAccountFormState(false));

        mAccountFormEnableState = new MutableLiveData<>();
        mAccountFormEnableState.setValue(new AccountFormEnableState(true, false, false, true, true, true, false));

        mUnmatchedPassword = new MutableLiveData<>();
        mUnmatchedPassword.setValue(false);

        mUserUpdated = new MutableLiveData<>();
        mUserUpdated.setValue(UserUpdatedState.NONE);

        // Get the user data
        mUserData = UserRepository.getInstance().getUser();
    }

    /**
     * Update the store details
     * @param pair updated Store object and the original password for validation
     */
    public void updateStore(Pair<Store, String> pair) {

        final Store oriStore = (Store) mUserRepository.getUserValue();
        final Store store = pair.first;
        store.setId(oriStore.getId());

        // If the change password was not enabled
        if (!mAccountFormEnableState.getValue().isChangePasswordEnabled()) {

            store.setPasswordSalt(oriStore.getPasswordSalt());
            store.setPassword(oriStore.getPassword());
            mUserRepository.update(store, new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {

                    // Update the store details without changing the password
                    if (mCheckOutViewModel != null)
                        if (oriStore.getPointsPerPrice() != store.getPointsPerPrice())
                            mCheckOutViewModel.updatePoint(null, true);

                    StoreAccountViewModel.this.onSuccess(o);
                }
            });

        } else {

            // Hash the password
            String hashedPassword = PasswordHasher.hash(pair.second, oriStore.getPasswordSalt());

            // Check if the original password entered by the user matches the original store password
            if (!hashedPassword.equals(oriStore.getPassword())) {
                mUnmatchedPassword.setValue(true);
            } else {

                store.setPasswordSalt(PasswordHasher.generateRandomSalt());
                store.setPassword(PasswordHasher.hash(store.getPassword(), store.getPasswordSalt()));

                // Update the store details with and change the password in the database
                mUserRepository.update(store, new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {

                        if (mCheckOutViewModel != null)
                            mCheckOutViewModel.updatePoint(null, true);

                        StoreAccountViewModel.this.onSuccess(o);
                    }
                });
            }
        }

        // If the name, address or points per price was changed, update to the Point (Membership) repository and database
        if (!oriStore.getName().equals(store.getName()) || !oriStore.getAddress().equals(store.getAddress()) || oriStore.getPointsPerPrice() != store.getPointsPerPrice()) {
            PointRepository.updateStoreDetails(store.getId(), store);

            // If the name was changed, update to the Transaction repository and database
            if (!oriStore.getName().equals(store.getName()))
                TransactionRepository.updateStoreName(store.getId(), store.getName());
        }
    }

    /**
     * Insert the store into the database
     * @param store store to be added
     */
    public void insertStore(final Store store) {

        store.setEmail(store.getEmail().toLowerCase());

        // Hash the password
        store.setPasswordSalt(PasswordHasher.generateRandomSalt());
        store.setPassword(PasswordHasher.hash(store.getPassword(), store.getPasswordSalt()));

        mUserRepository.get(store.getEmail(), new RegisterInterface() {
            @Override
            public void isUsernameValid(boolean isValid) {

                // If the username (email) is valid, insert the seller, otherwise prompt error
                if (isValid)
                    mUserRepository.insert(store, StoreAccountViewModel.this);
                else
                    mUserUpdated.setValue(UserUpdatedState.FAILED);
            }
        });
    }

    /**
     * Validate the store account form
     * @param name name to be validated
     * @param email email to be validated
     * @param password password to be validated
     * @param originalPassword original password to be validated
     * @param newPassword new password to be validated
     * @param address address to be validated
     * @param pointsPerPrice pointsPerPrice to be validated
     */
    public void storeAccountFormChanged(String name, String email, String password, String originalPassword, String newPassword, String address, String pointsPerPrice) {

        // Name cannot be empty
        if (name.trim().isEmpty()) {
            mStoreAccountFormState.setValue(new StoreAccountFormState(R.string.invalid_name, null, null, null, null, null, null));
            return;
        }

        // Registration
        if (mStoreId == null) {

            // Invalid email
            if (!isEmailValid(email)) {
                mStoreAccountFormState.setValue(new StoreAccountFormState(null, R.string.invalid_email, null, null, null, null, null));
                return;
            }

            // Invalid password
            if (!isPasswordValid(password)) {
                mStoreAccountFormState.setValue(new StoreAccountFormState(null, null, R.string.invalid_password, null, null, null, null));
                return;
            }
        } else if (mAccountFormEnableState.getValue().isChangePasswordEnabled()) {

            // Invalid original password
            if (!isPasswordValid(originalPassword)) {
                mStoreAccountFormState.setValue(new StoreAccountFormState(null, null, null, R.string.invalid_password, null, null, null));
                return;
            }

            // Invalid change password
            if (!isPasswordValid(newPassword)) {
                mStoreAccountFormState.setValue(new StoreAccountFormState(null, null, null, null, R.string.invalid_password, null, null));
                return;
            }
        }

        int pointsPerPriceParsed = 0;
        boolean isPointsPerPriceValid = true;

        try {
            pointsPerPriceParsed = Integer.parseInt(pointsPerPrice);

            // Check points per price range
            if (pointsPerPriceParsed <= 0 || pointsPerPriceParsed > 9999)
                isPointsPerPriceValid = false;
        } catch (Exception e) {
            isPointsPerPriceValid = false;
        }

        // Check address is empty
        if (address.trim().isEmpty()) {
            mStoreAccountFormState.setValue(new StoreAccountFormState(null, null, null, null, null, R.string.invalid_address, null));
            return;
        }

        // Check points per price is valid
        if (!isPointsPerPriceValid) {
            mStoreAccountFormState.setValue(new StoreAccountFormState(null, null, null, null, null, null, R.string.invalid_points_per_price));
            return;
        }

        // Everything is valid
        mStoreAccountFormState.setValue(new StoreAccountFormState(true));
    }

    /**
     * Set the account form enable state when the check password switch is enabled
     * @param changePasswordEnabled password switch is enabled
     */
    public void setEnableChangePassword(boolean changePasswordEnabled) {
        if (mStoreId != null && changePasswordEnabled)
            mAccountFormEnableState.setValue(new AccountFormEnableState(true, false, false, true, true, true, true));
        else
            mAccountFormEnableState.setValue(new AccountFormEnableState(true, false, false, true, true, true, false));
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
