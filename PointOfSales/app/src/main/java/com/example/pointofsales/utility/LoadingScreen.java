package com.example.pointofsales.utility;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

/**
 * LoadingScreen utility class to show and remove the progress bar while preventing the user operations on loading
 */
public class LoadingScreen {

    private Activity mActivity;
    private ProgressBar mProgressBar;

    public LoadingScreen(Activity activity, ProgressBar progressBar) {
        mActivity = activity;
        mProgressBar = progressBar;
    }

    /**
     * Start loading
     * Show progress bar
     * Stop user operation on the window
     */
    public void start() {
        mProgressBar.setVisibility(View.VISIBLE);
        mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    /**
     * Stop loading
     * Hide progress bar
     * Enables user operation on the window
     */
    public void end() {
        mProgressBar.setVisibility(View.GONE);
        mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
