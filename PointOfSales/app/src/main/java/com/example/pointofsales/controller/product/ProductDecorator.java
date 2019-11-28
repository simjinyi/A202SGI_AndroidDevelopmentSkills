package com.example.pointofsales.controller.product;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductDecorator extends RecyclerView.ItemDecoration {

    private int mDimen;

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);

        if (position == 0 || position == 1)
            outRect.top = mDimen;

        if (position % 2 == 0) {
            outRect.left = mDimen;
            outRect.right = mDimen / 2;
        } else {
            outRect.left = mDimen / 2;
            outRect.right = mDimen;
        }

        outRect.bottom = mDimen;
    }

    public ProductDecorator(float dimen) {
        mDimen = (int) dimen;
    }
}
