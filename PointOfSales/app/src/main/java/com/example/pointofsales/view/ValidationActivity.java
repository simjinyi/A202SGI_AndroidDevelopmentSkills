package com.example.pointofsales.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pointofsales.utility.ConfirmationDialog;
import com.example.pointofsales.utility.ConnectivityChecker;
import com.example.pointofsales.utility.ConnectivityCheckerInterface;
import com.example.pointofsales.view.login.LoginActivity;

public abstract class ValidationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onCreateValidated(savedInstanceState);

        new ConnectivityChecker(new ConnectivityCheckerInterface() {
            @Override
            public void isInternetAvailable(boolean internetAvailability) {
                if (!internetAvailability)
                    onError();
            }
        }).execute();
    }

    protected abstract void onCreateValidated(@Nullable Bundle savedInstanceState);

    protected void onError() {
        ConfirmationDialog.getConfirmationDialog(this, "No Internet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ValidationActivity.this.recreate();
            }
        }).show();
    }
}
