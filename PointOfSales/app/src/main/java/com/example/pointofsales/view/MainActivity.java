package com.example.pointofsales.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.pointofsales.R;
import com.example.pointofsales.view.login.LoginActivity;
import com.example.pointofsales.viewmodel.LoginViewModel;
import com.example.pointofsales.viewmodel.ProductViewModel;
import com.example.pointofsales.viewmodel.ProductViewModelFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends UserValidationActivity {

    private ProductViewModel mProductViewModel;

    @Override
    protected void onCreateValidated(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        mProductViewModel = ViewModelProviders.of(this, new ProductViewModelFactory(getStoreId())).get(ProductViewModel.class);

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_product)
                // R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getSupportFragmentManager().popBackStack();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
