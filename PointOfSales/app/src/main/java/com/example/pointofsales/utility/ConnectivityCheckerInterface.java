package com.example.pointofsales.utility;

/**
 * Interface to allow callback when the internet connection check is completed
 */
public interface ConnectivityCheckerInterface {
    void isInternetAvailable(boolean internetAvailability);
}
