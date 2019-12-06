package com.example.pointofsales.repository;

/**
 * Callback interface to determine if the point exists for the user
 */
public interface PointInterface {
    void pointExistCallback(boolean existence);
}
