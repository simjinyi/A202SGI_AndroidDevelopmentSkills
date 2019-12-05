package com.example.pointofsales.view.transaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Transaction;
import com.example.pointofsales.view.OnSingleClickListener;
import com.example.pointofsales.viewmodel.TransactionViewModel;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private TransactionViewModel mTransactionViewModel;

    private ArrayList<Transaction> mTransactions;

    public class TransactionHolder extends RecyclerView.ViewHolder {

        private TextView mTvTransactionDate;
        private TextView mTvTransactionPrice;
        private ImageButton mIbViewDetails;

        public TransactionHolder(@NonNull View itemView) {
            super(itemView);

            mTvTransactionDate = itemView.findViewById(R.id.tvTransactionDate);
            mTvTransactionPrice = itemView.findViewById(R.id.tvTransactionPrice);
            mIbViewDetails = itemView.findViewById(R.id.ibViewDetails);
        }

        public void bindData(Transaction transaction) {

            mTvTransactionDate.setText(String.valueOf(transaction.getTimestamp()));
            mTvTransactionPrice.setText(String.valueOf(transaction.getTotal()));
            mIbViewDetails.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    Toast.makeText(mContext, "Image Button Clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public TransactionAdapter(Context context, TransactionViewModel transactionViewModel) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mTransactionViewModel = transactionViewModel;
        mTransactions = mTransactionViewModel.getTransactions().getValue();
    }

    @NonNull
    @Override
    public TransactionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionHolder(mLayoutInflater.inflate(R.layout.list_item_transaction, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionHolder holder, int position) {
        holder.bindData(mTransactions.get(position));
    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }
}
