package com.example.pointofsales.view.login;

import com.google.firebase.database.DataSnapshot;

/**
 * LoginInterface callback to check if the user was found and the password will be passed back for validation
 */
public interface LoginInterface {
    void onLogin(boolean status, DataSnapshot dataSnapshot, String password);
}
