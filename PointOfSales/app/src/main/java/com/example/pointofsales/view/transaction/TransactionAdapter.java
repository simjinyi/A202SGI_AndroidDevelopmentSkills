package com.example.pointofsales.view.transaction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Transaction;
import com.example.pointofsales.model.UserType;
import com.example.pointofsales.view.OnSingleClickListener;
import com.example.pointofsales.viewmodel.TransactionViewModel;
import com.example.pointofsales.viewmodel.UserViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * TransactionAdapter extends from the RecyclerView.Adapter
 *
 */
public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionHolder> implements Filterable {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private TransactionViewModel mTransactionViewModel;

    private ArrayList<Transaction> mTransactions;
    private ViewDetailsButtonClick mViewDetailsButtonClick;

    /**
     * ViewHolder of the transaction data
     */
    public class TransactionHolder extends RecyclerView.ViewHolder {

        // View components
        private TextView mTvTransactionDate;
        private TextView mTvTransactionPrice;
        private TextView mTvCustomer;
        private TextView mTvSeller;
        private ImageButton mIbViewDetails;

        public TransactionHolder(@NonNull View itemView) {
            super(itemView);

            // Assign the reference to the view components
            mTvTransactionDate = itemView.findViewById(R.id.tvTransactionDate);
            mTvTransactionPrice = itemView.findViewById(R.id.tvTransactionPrice);
            mTvCustomer = itemView.findViewById(R.id.tvCustomer);
            mTvSeller = itemView.findViewById(R.id.tvSeller);
            mIbViewDetails = itemView.findViewById(R.id.ibViewDetails);
        }

        /**
         * Bind the transaction data to the view
         * @param transaction transaction object to be assigned to the view
         * @param position position of the adapter
         */
        public void bindData(Transaction transaction, final int position) {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            mTvTransactionDate.setText(simpleDateFormat.format(new Date(transaction.getTimestamp())));
            mTvTransactionPrice.setText(mContext.getString(R.string.tvTransactionPrice, transaction.getTotal()));

            // Set the user name if exists
            if (transaction.getUserName() != null)
                mTvCustomer.setText(transaction.getUserName());
            else
                mTvCustomer.setText(mContext.getString(R.string.default_customer));

            mTvSeller.setText(transaction.getStoreName());

            // View details button clicked
            mIbViewDetails.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {

                    // Callback the interface with the transaction object passed
                    mViewDetailsButtonClick.onViewDetailsButtonClick(mTransactions.get(position));
                }
            });

            // Show or hide the seller or customer based on the type of the user logged in
            if (UserViewModel.getUser().getType().equals(UserType.SELLER)) {
                mTvSeller.setVisibility(View.GONE);
                mTvCustomer.setVisibility(View.VISIBLE);
            } else {
                mTvSeller.setVisibility(View.VISIBLE);
                mTvCustomer.setVisibility(View.GONE);
            }
        }
    }

    public TransactionAdapter(Context context, TransactionViewModel transactionViewModel, ViewDetailsButtonClick viewDetailsButtonClick) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mTransactionViewModel = transactionViewModel;
        mTransactions = mTransactionViewModel.getTransactions().getValue();
        mViewDetailsButtonClick = viewDetailsButtonClick;
    }

    @NonNull
    @Override
    public TransactionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionHolder(mLayoutInflater.inflate(R.layout.list_item_transaction, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionHolder holder, int position) {
        holder.bindData(mTransactions.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mTransactions.size();
    }

    @Override
    public long getItemId(int position) {
        return mTransactions.get(position).hashCode();
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        mTransactions = transactions;
    }

    /**
     * Filters the transaction items
     * @return TransactionFilter to filter the transactions based on the query string
     */
    @Override
    public Filter getFilter() {
        return new TransactionFilter(mTransactionViewModel, this);
    }
}
