package com.example.pointofsales.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Transaction;
import com.example.pointofsales.model.state.TransactionLoadState;
import com.example.pointofsales.model.sort.TransactionSort;
import com.example.pointofsales.repository.TransactionInterface;
import com.example.pointofsales.repository.TransactionRepository;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class TransactionViewModel extends ViewModel implements TransactionInterface, ChildEventListener {

    private TransactionSort mTransactionSort;
    private MutableLiveData<ArrayList<Transaction>> mTransactions;
    private MutableLiveData<Float> mTotalTransaction;
    private MutableLiveData<Integer> mTransactionIndexDeleted;
    private MutableLiveData<TransactionLoadState> mTransactionLoadState;

    private TransactionRepository mTransactionRepository;

    public TransactionViewModel() {
        mTransactionRepository = TransactionRepository.getInstance(UserViewModel.getUser(), this, this);
        mTransactionRepository.setTransactionInterface(this);
        mTransactionRepository.setChildEventListener(this);
        mTransactions = mTransactionRepository.getTransactions();
        mTransactionSort = new TransactionSort();
        mTotalTransaction = new MutableLiveData<>();
        mTotalTransaction.setValue(0.0f);

        mTransactionIndexDeleted = new MutableLiveData<>();
        mTransactionIndexDeleted.setValue(-1);

        mTransactionLoadState = new MutableLiveData<>();
        mTransactionLoadState.setValue(TransactionLoadState.LOADING);
        checkTransactionExists();
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
            case CUSTOMER_DESC:
                mTransactionRepository.sortCustomerDesc();
                return R.string.customerDescending;
            case SELLER_ASC:
                mTransactionRepository.sortSellerAsc();
                return R.string.sellerAscending;
            default:
                mTransactionRepository.sortSellerDesc();
                return R.string.sellerDescending;
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

    public void clearTransactionIndexDeleted() {
        mTransactionIndexDeleted.setValue(-1);
    }

    private void checkTransactionExists() {
        mTransactionRepository.check(UserViewModel.getUserId(), UserViewModel.getUser().getType(), this);
    }

    public LiveData<ArrayList<Transaction>> getTransactions() {
        return mTransactions;
    }
    public LiveData<Float> getTotalTransaction() {
        return mTotalTransaction;
    }
    public LiveData<Integer> getTransactionIndexDeleted() {
        return mTransactionIndexDeleted;
    }
    public LiveData<TransactionLoadState> getTransactionLoadState() {
        return mTransactionLoadState;
    }

    @Override
    public void getDeletedIndex(int index) {
        mTransactionIndexDeleted.setValue(index);
    }

    @Override
    public void transactionExistCallback(boolean existence) {
        mTransactionLoadState.setValue(existence ? TransactionLoadState.LOADED : TransactionLoadState.NO_TRANSACTION);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        checkTransactionExists();
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        checkTransactionExists();
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        checkTransactionExists();
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
