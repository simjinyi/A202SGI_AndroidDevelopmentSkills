package com.example.pointofsales.view.product;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;

/**
 * ProductGridLayoutManager extends GridLayoutManager to allow animation on ViewHolder change
 */
public class ProductGridLayoutManager extends GridLayoutManager {
    public ProductGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }
}
