package com.example.pointofsales.controller.checkout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pointofsales.R;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public class CartHolder extends RecyclerView.ViewHolder {
        public CartHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public CartAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartHolder(mLayoutInflater.inflate(R.layout.cart_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }
}