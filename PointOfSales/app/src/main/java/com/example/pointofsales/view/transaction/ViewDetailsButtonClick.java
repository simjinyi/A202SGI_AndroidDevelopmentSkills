package com.example.pointofsales.view.transaction;

import com.example.pointofsales.model.Transaction;

/**
 * ViewDetailsButtonClick to call back when the view transaction details button in the RecyclerView was clicked
 */
public interface ViewDetailsButtonClick {
    void onViewDetailsButtonClick(Transaction transaction);
}
