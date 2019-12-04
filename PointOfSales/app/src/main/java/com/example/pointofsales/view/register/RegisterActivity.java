package com.example.pointofsales.view.register;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.pointofsales.R;
import com.google.android.material.tabs.TabLayout;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        RegisterTabAdapter tabsPagerAdapter = new RegisterTabAdapter(getSupportFragmentManager(), 0, this);

        ViewPager viewPager = findViewById(R.id.vpRegister);
        viewPager.setAdapter(tabsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabRegister);
        tabs.setupWithViewPager(viewPager);
    }
}
