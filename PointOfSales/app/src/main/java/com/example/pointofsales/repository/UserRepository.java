package com.example.pointofsales.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.pointofsales.database.UserDatabase;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.UserType;
import com.example.pointofsales.view.checkout.ScanListener;
import com.example.pointofsales.view.login.LoginInterface;
import com.example.pointofsales.view.register.RegisterInterface;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * UserRepository Singleton Class
 */
public class UserRepository {

    // Provides an interface for the View components to observe the changes made on the User profile (for both seller and customer)
    private MutableLiveData<User> mUser;

    private static UserRepository sUserRepository;

    private UserRepository() {
        mUser = new MutableLiveData<>();
        mUser.setValue(new User());
    }

    public static UserRepository getInstance() {
        if (sUserRepository == null)
            sUserRepository = new UserRepository();
        return sUserRepository;
    }

    // GETTER METHOD
    public User getUserValue() {
        return mUser.getValue();
    }
    // END GETTER METHOD

    /**
     * Update the user object in the database
     * @param user user object to be updated
     * @param onSuccessListener callback on successful operation
     */
    public void update(final User user, final OnSuccessListener onSuccessListener) {
        UserDatabase.getInstance().update(UserDatabase.Converter.userToMap(user), new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                mUser.setValue(user);
                onSuccessListener.onSuccess("success");
            }
        });
    }

    /**
     * Insert the user into the database
     * @param user user object to be inserted
     * @param onSuccessListener callback on successful operation
     */
    public void insert(final User user, final OnSuccessListener onSuccessListener) {
        UserDatabase.getInstance().insert(UserDatabase.Converter.userToMap(user), new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                mUser.setValue(user);
                onSuccessListener.onSuccess("success");
            }
        });
    }

    /**
     * Get the user object from the database on the given email
     * @param username username (email) to be used in getting the user object from the database
     * @param registerInterface callback on successful operation
     */
    public void get(String username, RegisterInterface registerInterface) {
        UserDatabase.getInstance().get(username, registerInterface);
    }

    /**
     * Get the user object from the database on the given userId
     * @param userId userId to be obtained from the database
     * @param userType type of the user to be obtained from the database
     * @param scanListener callback upon successful database operation
     */
    public void get(final String userId, final UserType userType, final ScanListener scanListener) {
        UserDatabase.getInstance().get(userId, userType, scanListener);
    }

    /**
     * Get the user object from the database on the given username (email)
     * @param username username (email) to be used in obtaining the user object
     * @param password password to be used in the loginInterface callback to check the validity of the user
     * @param loginInterface callback on successful database operation
     */
    public void login(String username, String password, LoginInterface loginInterface) {
        UserDatabase.getInstance().get(username, password, loginInterface);
    }

    /**
     * Notify the observers on the changes made on the User profile
     */
    public void notifyObservers() {
        mUser.setValue(mUser.getValue());
    }

    // GETTER METHOD
    public MutableLiveData<User> getUser() {
        return mUser;
    }
    // END GETTER METHOD

    /**
     * Clear the instance of the User repository on user logout to prepare for a new session
     */
    public static void clearInstance() {
        sUserRepository = null;
        UserDatabase.clearInstance();
    }
}
