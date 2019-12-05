package com.example.pointofsales.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.model.Transaction;
import com.example.pointofsales.repository.TransactionRepository;

import java.util.ArrayList;

public class TransactionViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Transaction>> mTransactions;

    private TransactionRepository mTransactionRepository;

    public TransactionViewModel() {
        mTransactionRepository = TransactionRepository.getInstance(UserViewModel.getUser());
        mTransactions = mTransactionRepository.getTransactions();
    }

    public LiveData<ArrayList<Transaction>> getTransactions() {
        return mTransactions;
    }
}
