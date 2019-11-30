package com.example.pointofsales.view.checkout;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CartDecorator extends RecyclerView.ItemDecoration {

    private int mDimen;
    private int mTotalItems;

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);

        if (position == mTotalItems - 1)
            outRect.bottom = mDimen;
    }

    public CartDecorator(float dimen, int totalItems) {
        mDimen = (int) dimen;
        mTotalItems = totalItems;
    }
}
