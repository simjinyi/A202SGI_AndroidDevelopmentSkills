package com.example.pointofsales.view.transaction;

import android.widget.Filter;

import com.example.pointofsales.model.Transaction;
import com.example.pointofsales.viewmodel.TransactionViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * TransactionFilter class to filter the Transactions in the Transaction RecyclerView
 */
public class TransactionFilter extends Filter {

    private TransactionViewModel mTransactionViewModel;
    private TransactionAdapter mTransactionAdapter;

    public TransactionFilter(TransactionViewModel transactionViewModel, TransactionAdapter transactionAdapter) {
        mTransactionViewModel = transactionViewModel;
        mTransactionAdapter = transactionAdapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        // Get the query string
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String searchString = constraint.toString();
        ArrayList<Transaction> transactions = mTransactionViewModel.getTransactions().getValue();
        ArrayList<Transaction> filteredTransactions = new ArrayList<>();

        if (searchString.isEmpty())
            filteredTransactions = transactions;
        else
            for (Transaction transaction : transactions)
                // If the date, total price, user name or store name contains the query string, add it to the filtredProduct list
                if (simpleDateFormat.format(new Date(transaction.getTimestamp())).contains(searchString.toLowerCase()) ||
                        String.format("%.2f", transaction.getTotal()).contains(searchString) ||
                        (transaction.getUserName() == null ? "-" : transaction.getUserName()).toLowerCase().contains(searchString.toLowerCase()) ||
                        transaction.getStoreName().toLowerCase().contains(searchString.toLowerCase()))
                    filteredTransactions.add(transaction);

        FilterResults filterResults = new FilterResults();
        filterResults.values = filteredTransactions;

        // Return the result
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        @SuppressWarnings("unchecked")
        ArrayList<Transaction> transactions = (ArrayList<Transaction>) results.values;
        mTransactionAdapter.setTransactions(transactions);

        // Notify the RecyclerView on the data changed and calculate the total transaction
        mTransactionAdapter.notifyDataSetChanged();
        mTransactionViewModel.calculateTotalTransaction(transactions);
    }
}
