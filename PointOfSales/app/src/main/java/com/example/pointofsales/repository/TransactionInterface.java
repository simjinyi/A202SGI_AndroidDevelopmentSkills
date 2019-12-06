package com.example.pointofsales.repository;

public interface TransactionInterface {
    void getDeletedIndex(int index);
    void transactionExistCallback(boolean existence);
}
