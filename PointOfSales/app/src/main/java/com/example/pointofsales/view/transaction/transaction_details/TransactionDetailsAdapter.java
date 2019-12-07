package com.example.pointofsales.view.transaction.transaction_details;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pointofsales.R;
import com.example.pointofsales.model.TransactionItem;
import com.example.pointofsales.viewmodel.TransactionViewModel;

/**
 * TransactionDetailsAdapter to populate the TransactionDetails RecyclerView
 */
public class TransactionDetailsAdapter extends RecyclerView.Adapter<TransactionDetailsAdapter.TransactionDetailsHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private TransactionViewModel mTransactionViewModel;
    private int mIndex;

    /**
     * ViewHolder of the TransactionDetails
     */
    public class TransactionDetailsHolder extends RecyclerView.ViewHolder {

        // View components
        private TextView mTvProductName;
        private TextView mTvProductPrice;
        private TextView mTvProductQuantity;
        private TextView mTvProductPriceExtension;

        public TransactionDetailsHolder(@NonNull View itemView) {
            super(itemView);

            // Assign the reference to the view components
            mTvProductName = itemView.findViewById(R.id.tvProductName);
            mTvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            mTvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            mTvProductPriceExtension = itemView.findViewById(R.id.tvProductPriceExtension);
        }

        /**
         * Bind the TransactionItem object to the view components
         * @param transactionItem transactionItem object to be assigned to the view
         */
        public void bindItem(TransactionItem transactionItem) {
            mTvProductName.setText(transactionItem.getName());
            mTvProductPrice.setText(String.format("%.2f", transactionItem.getPrice()));
            mTvProductQuantity.setText(String.valueOf(transactionItem.getQuantity()));
            mTvProductPriceExtension.setText(String.format("%.2f", transactionItem.getExtension()));
        }
    }

    public TransactionDetailsAdapter(Context context, TransactionViewModel transactionViewModel, int index) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mTransactionViewModel = transactionViewModel;
        mIndex = index;
    }

    @NonNull
    @Override
    public TransactionDetailsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionDetailsHolder(mLayoutInflater.inflate(R.layout.list_item_transaction_details, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionDetailsHolder holder, int position) {
        holder.bindItem(mTransactionViewModel.getTransactions().getValue().get(mIndex).getTransactionItems().get(position));
    }

    @Override
    public int getItemCount() {
        return mTransactionViewModel.getTransactions().getValue().get(mIndex).getTransactionItems().size();
    }
}