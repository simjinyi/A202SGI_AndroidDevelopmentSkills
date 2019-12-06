package com.example.pointofsales.repository;

import java.util.ArrayList;

/**
 * 
 */
public interface CartInterface {
    void notifyCartChanged(ArrayList<String> productNames);
}
