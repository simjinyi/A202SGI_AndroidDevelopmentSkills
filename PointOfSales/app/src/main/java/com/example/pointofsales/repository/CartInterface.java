package com.example.pointofsales.repository;

import java.util.ArrayList;

/**
 * CartInterface to provide a callback when the cart was updated
 */
public interface CartInterface {
    void notifyCartChanged(ArrayList<String> productNames);
}
