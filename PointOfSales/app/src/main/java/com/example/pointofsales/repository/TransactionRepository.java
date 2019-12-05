package com.example.pointofsales.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.pointofsales.database.PointDatabase;
import com.example.pointofsales.database.TransactionDatabase;
import com.example.pointofsales.model.Point;
import com.example.pointofsales.model.PointsRedeemedAndAwarded;
import com.example.pointofsales.model.Transaction;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.UserType;
import com.example.pointofsales.view.checkout.UpdatePointInterface;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class TransactionRepository {

    private static TransactionRepository sTransactionRepository;

    private TransactionRepository() {
    }

    public static TransactionRepository getInstance() {
        if (sTransactionRepository == null)
            sTransactionRepository = new TransactionRepository();
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

//    public void notifyObservers() {
//        mPoints.setValue(mPoints.getValue());
//    }

    public static void clearInstance() {
        sTransactionRepository = null;
        TransactionDatabase.clearInstance();
    }
}
