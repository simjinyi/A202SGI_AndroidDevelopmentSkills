package com.example.pointofsales.model;

public class User {

    private boolean mIsLoggedIn;

    private String mId;

    private String mEmail;
    private String mName;
    private String mPasswordSalt;
    private String mPassword;
    private UserType mType;

    public User() {
        mIsLoggedIn = false;
    }

    public boolean isLoggedIn() {
        return mIsLoggedIn;
    }
    public void setLoggedIn(boolean loggedIn) {
        mIsLoggedIn = loggedIn;
    }

    public String getId() {
        return mId;
    }
    public void setId(String id) {
        mId = id;
    }

    public String getEmail() {
        return mEmail;
    }
    public void setEmail(String email) {
        mEmail = email;
    }

    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }

    public String getPasswordSalt() {
        return mPasswordSalt;
    }
    public void setPasswordSalt(String passwordSalt) {
        mPasswordSalt = passwordSalt;
    }

    public String getPassword() {
        return mPassword;
    }
    public void setPassword(String password) {
        mPassword = password;
    }

    public UserType getType() {
        return mType;
    }
    public void setType(UserType type) {
        mType = type;
    }
}
