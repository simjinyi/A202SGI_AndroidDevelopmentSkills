package com.example.pointofsales.view.register;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.viewpager.widget.ViewPager;

import com.example.pointofsales.R;
import com.example.pointofsales.view.ValidationActivity;
import com.google.android.material.tabs.TabLayout;

/**
 * RegisterActivity handles customer and seller registration
 * Extends ValidationActivity to automatically check for internet availability before performing any operation
 */
public class RegisterActivity extends ValidationActivity {

    // View components
    ViewPager mVpRegister;
    TabLayout mTabRegister;

    @Override
    protected void onCreateValidated(Bundle savedInstanceState) {
        setContentView(R.layout.activity_register);

        // Set the title for the action bar
        getSupportActionBar().setTitle(R.string.title_activity_register);

        // Instantiate the RegisterTabAdapter
        RegisterTabAdapter tabsPagerAdapter = new RegisterTabAdapter(getSupportFragmentManager(), 0, this);

        // Set RegisterTabAdapter as the adapter for the ViewPager
        mVpRegister = findViewById(R.id.vpRegister);
        mVpRegister.setAdapter(tabsPagerAdapter);

        // Setup the tabs with the ViewPager view component
        mTabRegister = findViewById(R.id.tabRegister);
        mTabRegister.setupWithViewPager(mVpRegister);

        // Enable back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
