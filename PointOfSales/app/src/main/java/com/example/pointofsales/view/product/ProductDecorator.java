package com.example.pointofsales.view.product;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * ProductDecorator to decorate the Product list item in the RecyclerView
 * Ensures that the margin between the product list item is constant in every way
 */
public class ProductDecorator extends RecyclerView.ItemDecoration {

    private int mDimen;

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);

        // If the list item was on the first row, add a margin to the top
        if (position == 0 || position == 1)
            outRect.top = mDimen;

        // If the list item was on the left side, add a margin to the left and half margin to the right
        if (position % 2 == 0) {
            outRect.left = mDimen;
            outRect.right = mDimen / 2;
        } else {

            // If the list item was on the right side, add a margin to the right and half margin to the left
            outRect.left = mDimen / 2;
            outRect.right = mDimen;
        }

        // Add a margin to the bottom
        outRect.bottom = mDimen;
    }

    public ProductDecorator(float dimen) {
        mDimen = (int) dimen;
    }
}
