package com.example.pointofsales.repository;

/**
 * Transaction interface to get the deleted transaction index and provides a callback to check if at least one transaction exists
 */
public interface TransactionInterface {
    void getDeletedIndex(int index);
    void transactionExistCallback(boolean existence);
}
