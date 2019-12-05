package com.example.pointofsales.view.transaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pointofsales.R;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public class TransactionHolder extends RecyclerView.ViewHolder {
        public TransactionHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public TransactionAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public TransactionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionHolder(mLayoutInflater.inflate(R.layout.list_item_transaction, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }
}
