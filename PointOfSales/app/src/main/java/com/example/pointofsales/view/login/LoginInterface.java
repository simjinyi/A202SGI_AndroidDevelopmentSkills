package com.example.pointofsales.view.login;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

public interface LoginInterface {
    void onLogin(boolean status, DataSnapshot dataSnapshot, String password);
}
