package com.example.pointofsales.repository;

/**
 * ProductInterface acts as a callback to heck if at least one product exiss
 */
public interface ProductInterface {
    void productExistCallback(boolean existence);
}
