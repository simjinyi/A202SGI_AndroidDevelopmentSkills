package com.example.pointofsales.view.membership;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Point;
import com.example.pointofsales.model.UserType;
import com.example.pointofsales.viewmodel.MembershipViewModel;
import com.example.pointofsales.viewmodel.UserViewModel;

import java.util.ArrayList;

/**
 * MembershipAdapter to populate the RecyclerView
 */
public class MembershipAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private MembershipViewModel mMembershipViewModel;
    private ArrayList<Point> mPoints;

    /**
     * MembershipCustomerHolder holds the membership list item for the customer
     */
    public class MembershipCustomerHolder extends RecyclerView.ViewHolder implements CommonViewHolder {

        // View components
        private TextView mTvStoreName;
        private TextView mTvPoints;
        private TextView mTvStoreAddress;
        private TextView mTvPointsPerPrice;

        public MembershipCustomerHolder(@NonNull View itemView) {
            super(itemView);

            // Assign the reference to the view components
            mTvStoreName = itemView.findViewById(R.id.tvStoreName);
            mTvPoints = itemView.findViewById(R.id.tvPoints);
            mTvStoreAddress = itemView.findViewById(R.id.tvStoreAddress);
            mTvPointsPerPrice = itemView.findViewById(R.id.tvPointsPerPrice);
        }

        /**
         * Bind the point (membership) data to the view
         * @param point point object to be assigned
         * @param position position of the adapter
         */
        public void bindData(Point point, final int position) {
            mTvStoreName.setText(point.getStoreName());
            mTvPoints.setText(mContext.getString(R.string.tvPoints, point.getPoints()));
            mTvStoreAddress.setText(point.getStoreAddress());
            mTvPointsPerPrice.setText(mContext.getString(R.string.tvPointsPerPrice, point.getStorePointsPerPrice()));
        }
    }

    /**
     * MembershipSellerHolder holds the membership list item for the seller
     */
    public class MembershipSellerHolder extends RecyclerView.ViewHolder implements CommonViewHolder {

        private TextView mTvMemberName;
        private TextView mTvPoints;

        public MembershipSellerHolder(@NonNull View itemView) {
            super(itemView);

            // Assign the reference to the view components
            mTvMemberName = itemView.findViewById(R.id.tvMemberName);
            mTvPoints = itemView.findViewById(R.id.tvPoints);
        }

        /**
         * Bind the point (membership) data to the view
         * @param point point object to be assigned
         * @param position position of the adapter
         */
        public void bindData(Point point, final int position) {
            mTvMemberName.setText(point.getUserName());
            mTvPoints.setText(mContext.getString(R.string.tvPoints, point.getPoints()));
        }
    }

    public MembershipAdapter(Context context, MembershipViewModel membershipViewModel) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mMembershipViewModel = membershipViewModel;
        mPoints = membershipViewModel.getPoints().getValue();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (UserViewModel.getUser().getType().equals(UserType.SELLER))
            // Return MembershipSellerHolder object if the logged in user is a seller
            return new MembershipSellerHolder(mLayoutInflater.inflate(R.layout.list_item_membership_store, parent, false));
        else
            // Return MembershipCustomerHolder object if the logged in user is a customer
            return new MembershipCustomerHolder(mLayoutInflater.inflate(R.layout.list_item_membership, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Bind the data to the view components
        ((CommonViewHolder) holder).bindData(mPoints.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mPoints.size();
    }

    @Override
    public long getItemId(int position) {
        return mPoints.get(position).hashCode();
    }

    @Override
    public Filter getFilter() {
        // Return a MembershipFilter object to filter the membership based on the query string provided
        return new MembershipFilter(mMembershipViewModel, this);
    }

    public void setPoints(ArrayList<Point> points) {
        mPoints = points;
    }
}
