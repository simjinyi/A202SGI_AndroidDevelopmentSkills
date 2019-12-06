package com.example.pointofsales.view.membership;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pointofsales.viewmodel.MembershipViewModel;

public class MembershipDecoration extends RecyclerView.ItemDecoration {

    private int mDimen;
    private MembershipViewModel mMembershipViewModel;

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);

        if (position == mMembershipViewModel.getPoints().getValue().size() - 1)
            outRect.bottom = mDimen;
    }

    public MembershipDecoration(float dimen, MembershipViewModel membershipViewModel) {
        mDimen = (int) dimen;
        mMembershipViewModel = membershipViewModel;
    }
}
