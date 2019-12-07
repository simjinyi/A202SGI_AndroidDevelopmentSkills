package com.example.pointofsales.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pointofsales.model.User;
import com.example.pointofsales.repository.CartRepository;
import com.example.pointofsales.repository.MembershipRepository;
import com.example.pointofsales.repository.PointRepository;
import com.example.pointofsales.repository.ProductRepository;
import com.example.pointofsales.repository.TransactionRepository;
import com.example.pointofsales.repository.UserRepository;

public class UserViewModel extends ViewModel {

    // MutableLiveData observed by the View components
    private MutableLiveData<User> mUser;

    // Repository
    private UserRepository mUserRepository;

    public UserViewModel() {

        // Get the repository instance and the user MutableLiveData instance from the repository
        mUserRepository = UserRepository.getInstance();
        mUser = mUserRepository.getUser();
    }

    /**
     * Static method to check if the user is logged in
     * @return is the user is logged in
     */
    public static boolean isLoggedIn() {
        // Check through the UserRepository
        return UserRepository.getInstance().getUser().getValue().isLoggedIn();
    }

    /**
     * Static method to get the userId of the user logged in
     * @return the userId
     */
    public static String getUserId() {
        // Get through the UserRepository
        return UserRepository.getInstance().getUser().getValue().getId();
    }

    /**
     * Static method to get the User object logged in
     * @return the User object of the logged in user
     */
    public static User getUser() {
        // Get through the UserRepository
        return UserRepository.getInstance().getUser().getValue();
    }

    /**
     * Logs the user out from the system
     * Clears all the repository and database instance to prepare for a new login
     */
    public static void logout() {
        CartRepository.clearInstance();
        ProductRepository.clearInstance();
        UserRepository.clearInstance();
        PointRepository.clearInstance();
        TransactionRepository.clearInstance();
        MembershipRepository.clearInstance();
    }

    // GETTER METHOD
    public LiveData<User> getUserLiveData() {
        return mUser;
    }
    // END GETTER METHOD
}
