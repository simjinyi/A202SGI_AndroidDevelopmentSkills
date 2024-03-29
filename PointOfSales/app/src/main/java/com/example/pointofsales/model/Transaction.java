package com.example.pointofsales.model;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Model class to store the transaction details
 */
public class Transaction {

    private String mTransactionId;
    private String mUserName;
    private String mUserId;
    private String mStoreName;
    private String mStoreId;

    private long mTimestamp;
    private float mSubtotal;
    private Integer mPointsRedeemed;
    private Integer mPointsAwarded;
    private float mDiscount;

    // Comparators to be used in sorting the transactions
    // Date added ascending
    public static Comparator<Transaction> dateAscComparator = new Comparator<Transaction>() {
        @Override
        public int compare(Transaction o1, Transaction o2) {
            return Long.compare(o1.getTimestamp(), o2.getTimestamp());
        }
    };

    // Date added descending
    public static Comparator<Transaction> dateDescComparator = new Comparator<Transaction>() {
        @Override
        public int compare(Transaction o1, Transaction o2) {
            return Long.compare(o2.getTimestamp(), o1.getTimestamp());
        }
    };

    // Total price ascending
    public static Comparator<Transaction> priceAscComparator = new Comparator<Transaction>() {
        @Override
        public int compare(Transaction o1, Transaction o2) {
            return Float.compare(o1.getTotal(), o2.getTotal());
        }
    };

    // Total price descending
    public static Comparator<Transaction> priceDescComparator = new Comparator<Transaction>() {
        @Override
        public int compare(Transaction o1, Transaction o2) {
            return Float.compare(o2.getTotal(), o1.getTotal());
        }
    };

    // Customer name ascending
    public static Comparator<Transaction> customerAscComparator = new Comparator<Transaction>() {
        @Override
        public int compare(Transaction o1, Transaction o2) {

            String first = "", second = "";

            // Ensure that the names are not null before comparing
            if (o1.getUserName() == null)
                first = "-";
            else
                first = o1.getUserName();

            if (o2.getUserName() == null)
                second = "-";
            else
                second = o2.getUserName();

            return first.toLowerCase().compareTo(second.toLowerCase());
        }
    };

    // Customer name descending
    public static Comparator<Transaction> customerDescComparator = new Comparator<Transaction>() {
        @Override
        public int compare(Transaction o1, Transaction o2) {

            String first = "", second = "";

            // Ensure that the names are not null before comparing
            if (o1.getUserName() == null)
                first = "-";
            else
                first = o1.getUserName();

            if (o2.getUserName() == null)
                second = "-";
            else
                second = o2.getUserName();

            return second.toLowerCase().compareTo(first.toLowerCase());
        }
    };

    // Seller name ascending
    public static Comparator<Transaction> sellerAscComparator = new Comparator<Transaction>() {
        @Override
        public int compare(Transaction o1, Transaction o2) {
            return o1.getStoreName().toLowerCase().compareTo(o2.getStoreName().toLowerCase());
        }
    };

    // Seller name descending
    public static Comparator<Transaction> sellerDescComparator = new Comparator<Transaction>() {
        @Override
        public int compare(Transaction o1, Transaction o2) {
            return o2.getStoreName().toLowerCase().compareTo(o1.getStoreName().toLowerCase());
        }
    };
    // END Comparators

    public String getTransactionId() {
        return mTransactionId;
    }
    public void setTransactionId(String transactionId) {
        mTransactionId = transactionId;
    }

    private ArrayList<TransactionItem> mTransactionItems;

    public String getStoreName() {
        return mStoreName;
    }
    public void setStoreName(String storeName) {
        mStoreName = storeName;
    }

    public String getUserName() {
        return mUserName;
    }
    public void setUserName(String userName) {
        mUserName = userName;
    }

    public long getTimestamp() {
        return mTimestamp;
    }
    public void setTimestamp(long timestamp) {
        mTimestamp = timestamp;
    }

    public String getUserId() {
        return mUserId;
    }
    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getStoreId() {
        return mStoreId;
    }
    public void setStoreId(String storeId) {
        mStoreId = storeId;
    }

    public float getSubtotal() {
        return mSubtotal;
    }
    public void setSubtotal(float subtotal) {
        mSubtotal = subtotal;
    }

    public Integer getPointsRedeemed() {
        return mPointsRedeemed;
    }
    public void setPointsRedeemed(Integer pointsRedeemed) {
        mPointsRedeemed = pointsRedeemed;
    }

    public Integer getPointsAwarded() {
        return mPointsAwarded;
    }
    public void setPointsAwarded(Integer pointsAwarded) {
        mPointsAwarded = pointsAwarded;
    }

    public float getDiscount() {
        return mDiscount;
    }
    public void setDiscount(float discount) {
        mDiscount = discount;
    }

    public float getTotal() {
        return mSubtotal - mDiscount;
    }
    public int getPointsPerPrice() { return (int) (mPointsRedeemed / mDiscount); }

    public ArrayList<TransactionItem> getTransactionItems() {
        return mTransactionItems;
    }
    public void setTransactionItems(ArrayList<TransactionItem> transactionItems) {
        mTransactionItems = transactionItems;
    }
}
