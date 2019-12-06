package com.example.pointofsales.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Transaction;
import com.example.pointofsales.model.state.TransactionSort;
import com.example.pointofsales.repository.TransactionRepository;

import java.util.ArrayList;

public class TransactionViewModel extends ViewModel {

    private TransactionSort mTransactionSort;
    private MutableLiveData<ArrayList<Transaction>> mTransactions;
    private MutableLiveData<Float> mTotalTransaction;

    private TransactionRepository mTransactionRepository;

    public TransactionViewModel() {
        mTransactionRepository = TransactionRepository.getInstance(UserViewModel.getUser());
        mTransactions = mTransactionRepository.getTransactions();
        mTransactionSort = new TransactionSort();
        mTotalTransaction = new MutableLiveData<>();
        mTotalTransaction.setValue(0.0f);
    }

    public int sort() {
        switch (mTransactionSort.next()) {
            case DATE_ASC:
                mTransactionRepository.sortDateAsc();
                return R.string.dateAscending;
            case DATE_DESC:
                mTransactionRepository.sortDateDesc();
                return R.string.dateDescending;
            case PRICE_ASC:
                mTransactionRepository.sortPriceAsc();
                return R.string.priceAscending;
            case PRICE_DESC:
                mTransactionRepository.sortPriceDesc();
                return R.string.priceDescending;
            case CUSTOMER_ASC:
                mTransactionRepository.sortCustomerAsc();
                return R.string.customerAscending;
            default:
                mTransactionRepository.sortCustomerDesc();
                return R.string.customerDescending;
        }
    }

    public void calculateTotalTransaction(ArrayList<Transaction> transactions) {
        float total = 0.0f;

        for (Transaction transaction : transactions)
            total += transaction.getTotal();

        mTotalTransaction.setValue(total);
    }

    public int getTransactionIndexFromTransactionId(String transactionId) {
        return mTransactionRepository.getTransactionIndexByTransactionId(transactionId);
    }

    public LiveData<ArrayList<Transaction>> getTransactions() {
        return mTransactions;
    }
    public LiveData<Float> getTotalTransaction() {
        return mTotalTransaction;
    }
}
