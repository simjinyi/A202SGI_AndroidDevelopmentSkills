package com.example.pointofsales.repository;

import com.example.pointofsales.model.Product;

import java.util.ArrayList;

public interface CartInterface {
    void notifyCartChanged(ArrayList<String> productNames);
}
