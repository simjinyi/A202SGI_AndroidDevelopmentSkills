package com.example.pointofsales.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.pointofsales.database.UserDatabase;
import com.example.pointofsales.model.User;
import com.example.pointofsales.view.login.LoginInterface;
import com.example.pointofsales.view.register.RegisterInterface;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;

import java.util.Map;

public class UserRepository implements LoginInterface {

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
                onSuccessListener.onSuccess(o);
            }
        });
    }

    public void insert(final User user, final OnSuccessListener onSuccessListener) {
        UserDatabase.getInstance().insert(UserDatabase.Converter.userToMap(user), new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                mUser.setValue(user);
                onSuccessListener.onSuccess(o);
            }
        });
    }

    public void get(String username, RegisterInterface registerInterface) {
        UserDatabase.getInstance().get(username, registerInterface);
    }

    public void login(String username, String password) {
        UserDatabase.getInstance().get(username, password, this);
    }

    @Override
    public void onLogin(boolean status, DataSnapshot dataSnapshot, String password) {
        if (status) {
            if (dataSnapshot.exists()) {

                dataSnapshot = dataSnapshot.getChildren().iterator().next();

                User user = UserDatabase.Converter
                        .mapToUser(dataSnapshot.getKey().toString(), (Map<String, Object>) dataSnapshot.getValue());

                if (user.getPassword().equals(password)) {
                    user.setLoggedIn(true);
                    mUser.setValue(user);
                }
            }
        }

        notifyObservers();
    }

    public void notifyObservers() {
        mUser.setValue(mUser.getValue());
    }

    public MutableLiveData<User> getUser() {
        return mUser;
    }

    public static void clearInstance() {
        sUserRepository = null;
    }
}
