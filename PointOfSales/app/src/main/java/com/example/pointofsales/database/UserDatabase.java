package com.example.pointofsales.database;

import androidx.annotation.NonNull;

import com.example.pointofsales.model.Store;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.UserType;
import com.example.pointofsales.view.checkout.ScanListener;
import com.example.pointofsales.view.login.LoginInterface;
import com.example.pointofsales.view.register.RegisterInterface;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * UserDatabase Singleton Class
 */
public class UserDatabase {

    private static final String USER_COLLECTION = "user";
    private static UserDatabase sUserDatabase;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private UserDatabase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference(USER_COLLECTION);
    }

    public static UserDatabase getInstance() {
        if (sUserDatabase == null)
            sUserDatabase = new UserDatabase();
        return sUserDatabase;
    }

    /**
     * Get the user based on the username (email)
     * @param username email address to be used in obtaining the user
     * @param registerInterface callback to check if the username exists in the database (validation for registration)
     */
    public void get(String username, final RegisterInterface registerInterface) {
        mDatabaseReference.orderByChild("email")
                .equalTo(username)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        registerInterface.isUsernameValid(!dataSnapshot.exists());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        registerInterface.isUsernameValid(false);
                    }
                });
    }

    /**
     * Get the user from the database upon login
     * @param username username to be obtained from the database
     * @param password password for validation
     * @param loginInterface callback when data was obtained
     */
    public void get(String username, final String password, final LoginInterface loginInterface) {
        mDatabaseReference.orderByChild("email")
                .equalTo(username)
                .limitToFirst(1)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        loginInterface.onLogin(true, dataSnapshot, password);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        loginInterface.onLogin(false, null, password);
                    }
                });
    }

    /**
     * Get the user object during the QR code scanning
     * @param userId userId to be obtained from the database
     * @param userType do not add the user if it is a seller, as the membership applies for the customers only
     * @param scanListener callback when the user was successfully retrieved
     */
    public void get(final String userId, final UserType userType, final ScanListener scanListener) {
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Check to ensure that the content of the QR code does not include invalid characters which will crash the program
                if (userId.matches(".*[.#$\\[\\]]+.*")) {
                    scanListener.getUser(null);
                    return;
                }

                // User valid
                if (dataSnapshot.hasChild(userId)) {
                    mDatabaseReference.child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                User user = Converter.mapToUser(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue());

                                // If user is a seller, assign null
                                if (user.getType().equals(userType))
                                    scanListener.getUser(user);
                                else
                                    scanListener.getUser(null);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            scanListener.getUser(null);
                        }
                    });

                    // Invalid user
                } else {
                    scanListener.getUser(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Insert a new user into the database
     * @param user user to be inserted
     * @param onSuccessListener callback on successful operation
     */
    public void insert(Map<String, Object> user, OnSuccessListener onSuccessListener) {
        mDatabaseReference.push()
                .setValue(user)
                .addOnSuccessListener(onSuccessListener);
    }

    public void update(Map<String, Object> user, OnSuccessListener onSuccessListener) {
        mDatabaseReference.child(user.get("id").toString())
                .setValue(user)
                .addOnSuccessListener(onSuccessListener);
    }

    /**
     * Converter to ease database operation
     */
    public static class Converter {

        /**
         * Convert the User object into Map object
         * @param user object to be converted
         * @return Map object after converting the user
         */
        public static Map<String, Object> userToMap(User user) {

            Map<String, Object> hashMap = new HashMap<>();

            hashMap.put("id", user.getId());
            hashMap.put("email", user.getEmail());
            hashMap.put("name", user.getName());
            hashMap.put("passwordSalt", user.getPasswordSalt());
            hashMap.put("password", user.getPassword());
            hashMap.put("type", user.getType().name());

            // Add 2 attributes if the user is a seller
            if (user instanceof Store) {
                hashMap.put("address", ((Store) user).getAddress());
                hashMap.put("pointsPerPrice", ((Store) user).getPointsPerPrice());
            }

            return hashMap;
        }

        /**
         * Convert the map object into the User object
         * @param userId userId generated by the database
         * @param map map object to be converted
         * @return User object converted
         */
        public static User mapToUser(String userId, Map<String, Object> map) {

            User user = map.get("type").toString().equals("SELLER") ? new Store() : new User();

            user.setId(userId);
            user.setName(map.get("name").toString());
            user.setEmail(map.get("email").toString());
            user.setPasswordSalt(map.get("passwordSalt").toString());
            user.setPassword(map.get("password").toString());

            // Set the user type and assign the necessary additional values for the seller
            if (user instanceof Store) {
                user.setType(UserType.SELLER);
                ((Store) user).setAddress(map.get("address").toString());
                ((Store) user).setPointsPerPrice(Integer.parseInt(map.get("pointsPerPrice").toString()));
            } else {
                user.setType(UserType.CUSTOMER);
            }

            return user;
        }
    }

    /**
     * Clear the database instance upon logout
     */
    public static void clearInstance() {
        sUserDatabase = null;
    }
}
