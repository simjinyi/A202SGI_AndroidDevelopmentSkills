package com.example.pointofsales.view;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.pointofsales.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * MainActivity class handles all the fragments for the Seller
 * Extends UserValidationActivtity to ensure that the user was logged in before proceeding
 */
public class MainActivity extends UserValidationActivity {

    @Override
    protected void onCreateLoginValidated(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_product, R.id.navigation_membership, R.id.navigation_transaction)
                // R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();

        // Setup the NavController
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }
}
