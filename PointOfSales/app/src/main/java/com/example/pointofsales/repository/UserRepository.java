package com.example.pointofsales.repository;

import android.service.autofill.UserData;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.pointofsales.database.UserDatabase;
import com.example.pointofsales.model.Cart;
import com.example.pointofsales.model.Store;
import com.example.pointofsales.model.User;
import com.example.pointofsales.view.login.LoginInterface;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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

    public void login(String username, String password) {
        UserDatabase.getInstance().get(username, password, this);
    }

    public void logout() {
        if (mUser.getValue() instanceof Store)
            mUser.setValue(new Store());
        else
            mUser.setValue(new User());
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
}
