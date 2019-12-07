package com.example.pointofsales.view;

import android.os.SystemClock;
import android.view.View;

/**
 * OnSingleClickListener class ensures that the button cannot be spammed
 */
public abstract class OnSingleClickListener implements View.OnClickListener {

    private long mLastClickTime = 0;

    @Override
    public void onClick(View v) {

        // Validate the time to be half a second before a next click can be registered
        if (SystemClock.elapsedRealtime() - mLastClickTime < 500)
            return;

        onSingleClick(v);
        mLastClickTime = SystemClock.elapsedRealtime();
    }

    public abstract void onSingleClick(View v);
}
