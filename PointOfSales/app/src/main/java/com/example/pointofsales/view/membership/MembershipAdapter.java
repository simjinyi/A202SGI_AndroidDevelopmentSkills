package com.example.pointofsales.view.membership;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pointofsales.R;

public class MembershipAdapter extends RecyclerView.Adapter<MembershipAdapter.MembershipHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public class MembershipHolder extends RecyclerView.ViewHolder {
        public MembershipHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public MembershipAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public MembershipHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MembershipHolder(mLayoutInflater.inflate(R.layout.list_item_membership, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MembershipHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }
}
