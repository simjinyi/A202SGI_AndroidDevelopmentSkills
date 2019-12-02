package com.example.pointofsales.view;

import android.os.SystemClock;
import android.view.View;

public abstract class OnSingleClickListener implements View.OnClickListener {

    private long mLastClickTime = 0;

    @Override
    public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 500)
            return;
        onSingleClick(v);
        mLastClickTime = SystemClock.elapsedRealtime();
    }

    public abstract void onSingleClick(View v);
}
