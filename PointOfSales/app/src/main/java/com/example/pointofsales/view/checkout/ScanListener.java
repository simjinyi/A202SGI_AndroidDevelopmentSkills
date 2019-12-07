package com.example.pointofsales.view.checkout;

import com.example.pointofsales.model.User;

/**
 * ScanListener to assign the user obtained from the database after scanning the QR code
 */
public interface ScanListener {
    void getUser(User user);
}
