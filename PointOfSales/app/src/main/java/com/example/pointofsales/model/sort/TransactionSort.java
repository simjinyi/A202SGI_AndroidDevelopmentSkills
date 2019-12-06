package com.example.pointofsales.model.sort;

/**
 * Class to obtain the sorting method for the transactions
 */
public class TransactionSort {

    private int index = 0;
    private TransactionSortType[] mTransactionSortTypes = TransactionSortType.values();

    public TransactionSortType next() {
        return mTransactionSortTypes[index++ % mTransactionSortTypes.length];
    }
}
