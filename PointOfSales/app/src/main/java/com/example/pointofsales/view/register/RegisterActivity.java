package com.example.pointofsales.view.register;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.pointofsales.R;
import com.google.android.material.tabs.TabLayout;

public class RegisterActivity extends AppCompatActivity {

    ViewPager mVpRegister;
    TabLayout mTabRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        RegisterTabAdapter tabsPagerAdapter = new RegisterTabAdapter(getSupportFragmentManager(), 0, this);

        mVpRegister = findViewById(R.id.vpRegister);
        mVpRegister.setAdapter(tabsPagerAdapter);

        mTabRegister = findViewById(R.id.tabRegister);
        mTabRegister.setupWithViewPager(mVpRegister);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
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
