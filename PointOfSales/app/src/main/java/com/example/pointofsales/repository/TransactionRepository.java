package com.example.pointofsales.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.pointofsales.database.PointDatabase;
import com.example.pointofsales.database.TransactionDatabase;
import com.example.pointofsales.model.Store;
import com.example.pointofsales.model.Transaction;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.UserType;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * TransactionRepository Singleton Class
 * Implements ChildEventListener to listen to the changes from the database
 */
public class TransactionRepository implements ChildEventListener {

    private MutableLiveData<ArrayList<Transaction>> mTransactions; // Update the View observers dynamically on data change
    private User mUser;
    private ChildEventListener mChildEventListener;
    private TransactionInterface mTransactionInterface;

    private static TransactionRepository sTransactionRepository;

    private TransactionRepository(User user, TransactionInterface transactionInterface, ChildEventListener childEventListener) {
        mUser = user;
        mTransactionInterface = transactionInterface;

        mTransactions = new MutableLiveData<>();
        mTransactions.setValue(new ArrayList<Transaction>());
        TransactionDatabase.getInstance()
                .get(mUser.getId(), mUser.getType(), this);

        mChildEventListener = childEventListener;
    }

    public static TransactionRepository getInstance(User user, TransactionInterface transactionInterface, ChildEventListener childEventListener) {
        if (sTransactionRepository == null)
            sTransactionRepository = new TransactionRepository(user, transactionInterface, childEventListener);
        return sTransactionRepository;
    }

    /**
     * Check if the transaction exists for the given User
     * @param userId userId to be checked
     * @param userType type of the user to be checked (Seller or Customer)
     * @param transactionInterface callback on result from the database
     */
    public void check(String userId, UserType userType, final TransactionInterface transactionInterface) {
        TransactionDatabase.getInstance()
                .check(userId, userType, new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        transactionInterface.transactionExistCallback(dataSnapshot.exists());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        transactionInterface.transactionExistCallback(false);
                    }
                });
    }

    /**
     * SET the TransactionInterface for the Transaction ViewModel
     * @param transactionInterface transaction interface to be set
     */
    public void setTransactionInterface(TransactionInterface transactionInterface) {
        mTransactionInterface = transactionInterface;
    }

    /**
     * SET the ChildEventListener for the Transaction ViewModel
     * @param childEventListener childEventListener to be set
     */
    public void setChildEventListener(ChildEventListener childEventListener) {
        mChildEventListener = childEventListener;
    }

    /**
     * Provides an interface for the ViewModels to communicate with the database, in getting the data from the database
     * @param userId userId for the transactions to be obtained
     * @param userType user type for the transactions to be obtained
     * @param childEventListener callback on database result
     */
    public void get(String userId, UserType userType, ChildEventListener childEventListener) {
        TransactionDatabase.getInstance()
                .get(userId, userType, childEventListener);
    }

    /**
     * Provides an interface for the ViewModels to communicate with the database, in inserting new data into the database
     * @param transaction transaction to be inserted
     * @param onSuccessListener callback on successful operation
     */
    public void insert(Transaction transaction, OnSuccessListener onSuccessListener) {
        TransactionDatabase.getInstance()
                .insert(TransactionDatabase.Converter.transactionToMap(transaction), onSuccessListener);
    }

    /**
     * Update the user name in the Transaction database
     * @param userId userId to be updated
     * @param userName new name to be updated
     */
    public static void updateUserName(String userId, String userName) {
        TransactionDatabase.getInstance().updateUserName(userId, userName);
    }

    /**
     * Updates the store name in the Transaction database
     * @param storeId storeId to update
     * @param storeName new name to be updated
     */
    public static void updateStoreName(String storeId, String storeName) {
        TransactionDatabase.getInstance().updateStoreName(storeId, storeName);
    }

    // SORTER METHODS
    // Utilizes the comparators defined to sort the transactions
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

    public void sortSellerAsc() {
        Collections.sort(mTransactions.getValue(), Transaction.sellerAscComparator);
        notifyObservers();
    }

    public void sortSellerDesc() {
        Collections.sort(mTransactions.getValue(), Transaction.sellerDescComparator);
        notifyObservers();
    }
    // END SORTER METHODS

    /**
     * Notify the observers of the LiveData regarding the changes made to the transaction local storage to update the View dynamically
     */
    public void notifyObservers() {
        mTransactions.setValue(mTransactions.getValue());
    }

    /**
     * Clear repository instance upon logout to prepare for a new session
     */
    public static void clearInstance() {
        sTransactionRepository = null;
        TransactionDatabase.clearInstance();
    }

    // GETTER METHOD
    public MutableLiveData<ArrayList<Transaction>> getTransactions() {
        return mTransactions;
    }
    // END GETTER METHOD

    // Child Event Listeners
    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        if (dataSnapshot.exists()) {
            mTransactions.getValue().add(TransactionDatabase.Converter.mapToTransaction(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue()));

            // Cascade call to the child event listener of the caller if not null
            if (mChildEventListener != null)
                mChildEventListener.onChildAdded(dataSnapshot, s);

            notifyObservers();
        }
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        // Get the changed transaction
        Transaction changedTransaction = TransactionDatabase.Converter.mapToTransaction(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue());

        // Get the changed transaction index in the local storage (ArrayList) by finding the updated transaction index
        int changedTransactionIndex = getTransactionIndexByTransactionId(changedTransaction.getTransactionId());

        // Update the local storage
        mTransactions.getValue().set(changedTransactionIndex, changedTransaction);

        if (mChildEventListener != null)
            mChildEventListener.onChildChanged(dataSnapshot, s);

        notifyObservers();
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        // Get the removed transaction
        Transaction removedTransaction = TransactionDatabase.Converter.mapToTransaction(dataSnapshot.getKey(), (Map<String, Object>) dataSnapshot.getValue());

        // Get the removed transaction index in the local storage (ArrayList) by finding the removed transaction index
        int removedTransactionIndex = getTransactionIndexByTransactionId(removedTransaction.getTransactionId());
        mTransactions.getValue().remove(removedTransactionIndex);

        if (mChildEventListener != null)
            mChildEventListener.onChildRemoved(dataSnapshot);

        // Callback to notify the TransactionInterface regarding the index deleted
        if (mTransactionInterface != null)
            mTransactionInterface.getDeletedIndex(removedTransactionIndex);

        notifyObservers();
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        // Not used
        if (mChildEventListener != null)
            mChildEventListener.onChildMoved(dataSnapshot, s);
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        // Not used
        if (mChildEventListener != null)
            mChildEventListener.onCancelled(databaseError);
    }

    /**
     * Get the transaction from the given transactionId
     * @param transactionId transactionId to be compared
     * @return index of the transaction found in the local storage (ArrayList)
     */
    public int getTransactionIndexByTransactionId(String transactionId) {
        for (int i = 0; i < mTransactions.getValue().size(); i++)
            if (mTransactions.getValue().get(i).getTransactionId().equals(transactionId))
                return i;
        return -1;
    }
}
