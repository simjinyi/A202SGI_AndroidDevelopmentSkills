package com.example.pointofsales.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.pointofsales.R;

public class ConfirmationDialog {
    public static AlertDialog.Builder getConfirmationDialog(final Activity activity, String message, DialogInterface.OnClickListener confirmListener) {
        return new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.confirmation_dialog))
                .setMessage(message)
                .setIcon(R.drawable.ic_warning_24dp)
                .setPositiveButton(android.R.string.yes, confirmListener)
                .setNegativeButton(android.R.string.no, null);
    }
}
