package com.example.pointofsales.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Transaction;
import com.example.pointofsales.model.sort.TransactionSort;
import com.example.pointofsales.model.state.TransactionLoadState;
import com.example.pointofsales.repository.TransactionInterface;
import com.example.pointofsales.repository.TransactionRepository;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

/**
 * TransactionViewModel to handle the transaction operations
 * Implements the transaction interface to check if at least one transaction exists
 * Implements the child event listener to listen to changes in the transactions
 */
public class TransactionViewModel extends ViewModel implements TransactionInterface, ChildEventListener {

    private TransactionSort mTransactionSort;

    // MutableLiveData observed by the View components
    private MutableLiveData<ArrayList<Transaction>> mTransactions;
    private MutableLiveData<Float> mTotalTransaction;
    private MutableLiveData<Integer> mTransactionIndexDeleted;
    private MutableLiveData<TransactionLoadState> mTransactionLoadState;

    // Repository
    private TransactionRepository mTransactionRepository;

    public TransactionViewModel() {

        // Get the transaction repository instance
        mTransactionRepository = TransactionRepository.getInstance(UserViewModel.getUser(), this, this);

        // Set this object as the TransactionInterface and ChildEventListener
        mTransactionRepository.setTransactionInterface(this);
        mTransactionRepository.setChildEventListener(this);

        // Get the transactions
        mTransactions = mTransactionRepository.getTransactions();
        mTransactionSort = new TransactionSort();

        // Instantiate the MutableLiveData objects
        mTotalTransaction = new MutableLiveData<>();
        mTotalTransaction.setValue(0.0f);

        mTransactionIndexDeleted = new MutableLiveData<>();
        mTransactionIndexDeleted.setValue(-1);

        mTransactionLoadState = new MutableLiveData<>();
        mTransactionLoadState.setValue(TransactionLoadState.LOADING);

        // Check if at least one transaction exists
        checkTransactionExists();
    }

    /**
     * Sort function used in sorting the transactions in the Transaction RecyclerView
     * @return the String resource containing the way the transactions are sorted
     */
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

    /**
     * Calculate the total transaction price and update to the MutableLiveData
     * @param transactions list of transactions to be calculated
     */
    public void calculateTotalTransaction(ArrayList<Transaction> transactions) {
        float total = 0.0f;

        for (Transaction transaction : transactions)
            total += transaction.getTotal();

        mTotalTransaction.setValue(total);
    }

    /**
     * Get the transaction index in the ArrayList given the transactionId
     * @param transactionId transactionId to be searched
     * @return the transaction index in the ArrayList
     */
    public int getTransactionIndexFromTransactionId(String transactionId) {
        // Call to the repository to search for the transactionId
        return mTransactionRepository.getTransactionIndexByTransactionId(transactionId);
    }

    /**
     * Check if at least one transaction exists
     */
    private void checkTransactionExists() {
        // Call to the repository to check if the transaction exists
        mTransactionRepository.check(UserViewModel.getUserId(), UserViewModel.getUser().getType(), this);
    }

    // GETTER METHODS
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
    // END GETTER METHODS

    /**
     * Get and set the transaction deleted index to the MutableLiveData
     * @param index index of the transaction deleted
     */
    @Override
    public void getDeletedIndex(int index) {
        mTransactionIndexDeleted.setValue(index);
    }

    /**
     * Update the transaction load state based on the existence of the transaction
     * @param existence if at least one transaction exists
     */
    @Override
    public void transactionExistCallback(boolean existence) {
        mTransactionLoadState.setValue(existence ? TransactionLoadState.LOADED : TransactionLoadState.NO_TRANSACTION);
    }

    // Child Event Listeners
    // Check if the transaction exists for any changes in the dataset
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
        // ignore
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        // ignore
    }
    // END Child Event Listeners
}
