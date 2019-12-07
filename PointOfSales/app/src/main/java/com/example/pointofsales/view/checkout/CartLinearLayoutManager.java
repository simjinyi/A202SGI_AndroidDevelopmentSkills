package com.example.pointofsales.view.checkout;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * CartLinearLayoutManager extends LinearLayoutManager to allow animation on ViewHolder change
 */
public class CartLinearLayoutManager extends LinearLayoutManager {
    public CartLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }
}
