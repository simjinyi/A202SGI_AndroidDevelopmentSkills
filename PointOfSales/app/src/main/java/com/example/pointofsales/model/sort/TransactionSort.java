package com.example.pointofsales.model.sort;

public class TransactionSort {

    private int index = 0;
    private TransactionSortType[] mTransactionSortTypes = TransactionSortType.values();

    public TransactionSortType next() {
        return mTransactionSortTypes[index++ % mTransactionSortTypes.length];
    }
}
