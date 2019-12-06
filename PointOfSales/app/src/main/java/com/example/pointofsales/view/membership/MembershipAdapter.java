package com.example.pointofsales.view.membership;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Point;
import com.example.pointofsales.viewmodel.MembershipViewModel;

public class MembershipAdapter extends RecyclerView.Adapter<MembershipAdapter.MembershipHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private MembershipViewModel mMembershipViewModel;

    public class MembershipHolder extends RecyclerView.ViewHolder {

        private TextView mTvStoreName;
        private TextView mTvPoints;
        private TextView mTvStoreAddress;
        private TextView mTvPointsPerPrice;

        public MembershipHolder(@NonNull View itemView) {
            super(itemView);

            mTvStoreName = itemView.findViewById(R.id.tvStoreName);
            mTvPoints = itemView.findViewById(R.id.tvPoints);
            mTvStoreAddress = itemView.findViewById(R.id.tvStoreAddress);
            mTvPointsPerPrice = itemView.findViewById(R.id.tvPointsPerPrice);
        }

        public void bindData(Point point) {
            mTvStoreName.setText(point.getStoreName());
            mTvPoints.setText(mContext.getString(R.string.tvPoints, point.getPoints()));
            mTvStoreAddress.setText(point.getStoreAddress());
            mTvPointsPerPrice.setText(mContext.getString(R.string.tvPointsPerPrice, point.getStorePointsPerPrice()));
        }
    }

    public MembershipAdapter(Context context, MembershipViewModel membershipViewModel) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mMembershipViewModel = membershipViewModel;
    }

    @NonNull
    @Override
    public MembershipHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MembershipHolder(mLayoutInflater.inflate(R.layout.list_item_membership, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MembershipHolder holder, int position) {
        holder.bindData(mMembershipViewModel.getPoints().getValue().get(position));
    }

    @Override
    public int getItemCount() {
        return mMembershipViewModel.getPoints().getValue().size();
    }
}
