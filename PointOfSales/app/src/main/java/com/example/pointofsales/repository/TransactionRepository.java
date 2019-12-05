package com.example.pointofsales.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.pointofsales.database.TransactionDatabase;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.model.Transaction;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.UserType;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class TransactionRepository implements ChildEventListener {

    private MutableLiveData<ArrayList<Transaction>> mTransactions;
    private User mUser;

    private static TransactionRepository sTransactionRepository;

    private TransactionRepository(User user) {
        mUser = user;

        mTransactions = new MutableLiveData<>();
        mTransactions.setValue(new ArrayList<Transaction>());
        TransactionDatabase.getInstance()
                .get(mUser.getId(), mUser.getType(), this);
    }

    public static TransactionRepository getInstance(User user) {
        if (sTransactionRepository == null)
            sTransactionRepository = new TransactionRepository(user);
        return sTransactionRepository;
    }

    public void get(String userId, UserType userType, ChildEventListener childEventListener) {
        TransactionDatabase.getInstance()
                .get(userId, userType, childEventListener);
    }

    public void insert(Transaction transaction, OnSuccessListener onSuccessListener) {
        TransactionDatabase.getInstance()
                .insert(TransactionDatabase.Converter.transactionToMap(transaction), onSuccessListener);
    }

    public void sortDateAsc() {
        Collections.sort(mTransactions.getValue(), Transaction.dateAscComparator);
        notifyObservers();
    }

    public void sortDateDesc() {
        Collections.sort(mTransactions.getValue(), Transaction.dateDescComparator);
        notifyObservers();
    }

    public void sortPriceAsc() {
        Collections.sort(mTransactions.getValue(), Transaction.priceAscComparator);
        notifyObservers();
    }

    public void sortPriceDesc() {
        Collections.sort(mTransactions.getValue(), Transaction.priceDescComparator);
        notifyObservers();
    }

    public void sortCustomerAsc() {
        Collections.sort(mTransactions.getValue(), Transaction.customerAscComparator);
        notifyObservers();
    }

    public void sortCustomerDesc() {
        Collections.sort(mTransactions.getValue(), Transaction.customerDescComparator);
        notifyObservers();
    }

    public void notifyObservers() {
        mTransactions.setValue(mTransactions.getValue());
    }

    public static void clearInstance() {
        sTransactionRepository = null;
        TransactionDatabase.clearInstance();
    }

    public MutableLiveData<ArrayList<Transaction>> getTransactions() {
        return mTransactions;
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        if (dataSnapshot.exists()) {
            mTransactions.getValue().add(TransactionDatabase.Converter.mapToTransaction(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue()));
            notifyObservers();
        }
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
