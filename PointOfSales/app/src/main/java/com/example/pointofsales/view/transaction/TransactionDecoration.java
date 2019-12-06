package com.example.pointofsales.view.transaction;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pointofsales.viewmodel.TransactionViewModel;

public class TransactionDecoration extends RecyclerView.ItemDecoration {

    private int mDimen;
    private TransactionViewModel mTransactionViewModel;

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);

        if (position == mTransactionViewModel.getTransactions().getValue().size() - 1)
            outRect.bottom = mDimen;
    }

    public TransactionDecoration(float dimen, TransactionViewModel transactionViewModel) {
        mDimen = (int) dimen;
        mTransactionViewModel = transactionViewModel;
    }
}

