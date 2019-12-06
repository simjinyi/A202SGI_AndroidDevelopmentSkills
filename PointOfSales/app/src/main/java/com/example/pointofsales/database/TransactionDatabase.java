package com.example.pointofsales.database;

import com.example.pointofsales.model.Transaction;
import com.example.pointofsales.model.TransactionItem;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.UserType;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TransactionDatabase {

    private static final String TRANSACTION_COLLECTION = "transaction";
    private static TransactionDatabase sTransactionDatabase;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    private TransactionDatabase() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference(TRANSACTION_COLLECTION);
    }

    public static TransactionDatabase getInstance() {
        if (sTransactionDatabase == null)
            sTransactionDatabase = new TransactionDatabase();
        return sTransactionDatabase;
    }

    public void check(String userId, UserType userType, ValueEventListener valueEventListener) {
        if (userType.equals(UserType.SELLER))
            mDatabaseReference.orderByChild("storeId")
                    .equalTo(userId)
                    .addListenerForSingleValueEvent(valueEventListener);
        else
            mDatabaseReference.orderByChild("userId")
                    .equalTo(userId)
                    .addListenerForSingleValueEvent(valueEventListener);
    }

    public void get(String userId, UserType userType, ChildEventListener childEventListener) {
        if (userType.equals(UserType.SELLER))
            mDatabaseReference.orderByChild("storeId")
                    .equalTo(userId)
                    .addChildEventListener(childEventListener);
        else
            mDatabaseReference.orderByChild("userId")
                    .equalTo(userId)
                    .addChildEventListener(childEventListener);
    }

    public void insert(Map<String, Object> transaction, OnSuccessListener onSuccessListener) {
        mDatabaseReference.push()
                .setValue(transaction)
                .addOnSuccessListener(onSuccessListener);
    }

    public static class Converter {
        public static Map<String, Object> transactionToMap(Transaction transaction) {

            Map<String, Object> hashMap = new HashMap<>();

            hashMap.put("transactionId", transaction.getTransactionId());
            hashMap.put("userName", transaction.getUserName());
            hashMap.put("userId", transaction.getUserId());
            hashMap.put("storeName", transaction.getStoreName());
            hashMap.put("storeId", transaction.getStoreId());

            hashMap.put("timestamp", transaction.getTimestamp());
            hashMap.put("subTotal", transaction.getSubtotal());
            hashMap.put("pointsRedeemed", transaction.getPointsRedeemed());
            hashMap.put("pointsAwarded", transaction.getPointsAwarded());
            hashMap.put("discount", transaction.getDiscount());

            hashMap.put("product", new ArrayList<Map<String, Object>>());

            for (TransactionItem transactionItem : transaction.getTransactionItems())
                ((ArrayList<Map<String, Object>>) hashMap.get("product")).add(transactionItemToMap(transactionItem));

            return hashMap;
        }

        public static Transaction mapToTransaction(String transactionId, Map<String, Object> map) {

            Transaction transaction = new Transaction();

            transaction.setTransactionId(transactionId);

            if (map.get("userId") != null) {
                transaction.setUserId(map.get("userId").toString());
                transaction.setUserName(map.get("userName").toString());
            } else {
                transaction.setUserId(null);
                transaction.setUserName(null);
            }

            transaction.setStoreId(map.get("storeId").toString());
            transaction.setStoreName(map.get("storeName").toString());

            transaction.setTimestamp(Long.parseLong(map.get("timestamp").toString()));
            transaction.setSubtotal(Float.parseFloat(map.get("subTotal").toString()));

            if (map.get("pointsRedeemed") != null)
                transaction.setPointsRedeemed(Integer.parseInt(map.get("pointsRedeemed").toString()));
            else
                transaction.setPointsRedeemed(null);

            if (map.get("pointsAwarded") != null)
                transaction.setPointsAwarded(Integer.parseInt(map.get("pointsAwarded").toString()));
            else
                transaction.setPointsAwarded(null);

            transaction.setDiscount(Float.parseFloat(map.get("discount").toString()));

            transaction.setTransactionItems(new ArrayList<TransactionItem>());

            for (Map<String, Object> itemMap : (ArrayList<Map<String, Object>>) map.get("product"))
                transaction.getTransactionItems().add(mapToTransactionItem(itemMap));

            return transaction;
        }

        private static Map<String, Object> transactionItemToMap(TransactionItem transactionItem) {

            Map<String, Object> hashMap = new HashMap<>();

            hashMap.put("name", transactionItem.getName());
            hashMap.put("price", transactionItem.getPrice());
            hashMap.put("quantity", transactionItem.getQuantity());

            return hashMap;
        }

        private static TransactionItem mapToTransactionItem(Map<String, Object> map) {

            TransactionItem transactionItem = new TransactionItem();

            transactionItem.setName(map.get("name").toString());
            transactionItem.setPrice(Float.parseFloat(map.get("price").toString()));
            transactionItem.setQuantity(Integer.parseInt(map.get("quantity").toString()));

            return transactionItem;
        }
    }

    public static void clearInstance() {
        sTransactionDatabase = null;
    }
}
