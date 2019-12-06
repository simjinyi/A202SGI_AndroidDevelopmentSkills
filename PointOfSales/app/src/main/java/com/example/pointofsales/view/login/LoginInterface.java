package com.example.pointofsales.view.login;

import com.google.firebase.database.DataSnapshot;

public interface LoginInterface {
    void onLogin(boolean status, DataSnapshot dataSnapshot, String password);
}
