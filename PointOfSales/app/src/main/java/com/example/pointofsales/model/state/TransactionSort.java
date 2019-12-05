package com.example.pointofsales.model.state;

import com.example.pointofsales.model.ProductSortType;

public class TransactionSort {

    private int index = 0;
    private TransactionSortType[] mTransactionSortTypes = TransactionSortType.values();

    public TransactionSortType next() {
        return mTransactionSortTypes[index++ % mTransactionSortTypes.length];
    }
}
