package com.example.pointofsales.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.pointofsales.database.UserDatabase;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.UserType;
import com.example.pointofsales.view.checkout.ScanListener;
import com.example.pointofsales.view.login.LoginInterface;
import com.example.pointofsales.view.register.RegisterInterface;
import com.google.android.gms.tasks.OnSuccessListener;

public class UserRepository {

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

    public User getUserValue() {
        return mUser.getValue();
    }

    public void update(final User user, final OnSuccessListener onSuccessListener) {
        UserDatabase.getInstance().update(UserDatabase.Converter.userToMap(user), new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                mUser.setValue(user);
                onSuccessListener.onSuccess("success");
            }
        });
    }

    public void insert(final User user, final OnSuccessListener onSuccessListener) {
        UserDatabase.getInstance().insert(UserDatabase.Converter.userToMap(user), new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                mUser.setValue(user);
                onSuccessListener.onSuccess("success");
            }
        });
    }

    public void get(String username, RegisterInterface registerInterface) {
        UserDatabase.getInstance().get(username, registerInterface);
    }

    public void get(final String userId, final UserType userType, final ScanListener scanListener) {
        UserDatabase.getInstance().get(userId, userType, scanListener);
    }

    public void login(String username, String password, LoginInterface loginInterface) {
        UserDatabase.getInstance().get(username, password, loginInterface);
    }

    public void notifyObservers() {
        mUser.setValue(mUser.getValue());
    }

    public MutableLiveData<User> getUser() {
        return mUser;
    }

    public static void clearInstance() {
        sUserRepository = null;
        UserDatabase.clearInstance();
    }
}
