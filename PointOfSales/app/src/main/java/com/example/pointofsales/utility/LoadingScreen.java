package com.example.pointofsales.utility;

import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

public class LoadingScreen {

    private Activity mActivity;
    private ProgressBar mProgressBar;

    public LoadingScreen(Activity activity, ProgressBar progressBar) {
        mActivity = activity;
        mProgressBar = progressBar;
    }

    public void start() {
        mProgressBar.setVisibility(View.VISIBLE);
        mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void end() {
        mProgressBar.setVisibility(View.GONE);
        mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}