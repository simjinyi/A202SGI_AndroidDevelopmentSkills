package com.example.pointofsales.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pointofsales.R;
import com.example.pointofsales.utility.ConnectivityChecker;
import com.example.pointofsales.utility.ConnectivityCheckerInterface;

/**
 * ValidationActivity class to validate if there is an active internet connection
 */
public abstract class ValidationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onCreateValidated(savedInstanceState);

        // Check if the internet connection is valid
        new ConnectivityChecker(new ConnectivityCheckerInterface() {
            @Override
            public void isInternetAvailable(boolean internetAvailability) {
                if (!internetAvailability)
                    onError();
            }
        }).execute();
    }

    protected abstract void onCreateValidated(@Nullable Bundle savedInstanceState);

    // No internet connection
    protected void onError() {

        // Prompt uncancelable notification
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.no_internet_connection))
                .setMessage(getString(R.string.no_internet_connection_description))
                .setIcon(R.drawable.ic_warning_24dp)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.reload), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Reload activity
                        ValidationActivity.this.recreate();
                    }
                })
                .setNegativeButton(getString(R.string.terminate), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Terminate activity
                        ValidationActivity.this.finishAffinity();
                    }
                }).show();
    }
}
