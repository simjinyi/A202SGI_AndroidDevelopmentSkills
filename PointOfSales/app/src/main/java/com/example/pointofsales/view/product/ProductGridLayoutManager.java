package com.example.pointofsales.view.product;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;

public class ProductGridLayoutManager extends GridLayoutManager {
    public ProductGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }
}
