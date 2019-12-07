package com.example.pointofsales.view.transaction.transaction_details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Transaction;
import com.example.pointofsales.model.state.TransactionLoadState;
import com.example.pointofsales.view.transaction.TransactionFragment;
import com.example.pointofsales.viewmodel.TransactionViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TransactionDetailsFragment shows the transaction details with the items purchased
 */
public class TransactionDetailsFragment extends Fragment {

    private int mIndex;

    // ViewModel object
    private TransactionViewModel mTransactionViewModel;
    private TransactionDetailsAdapter mTransactionDetailsAdapter;

    // View components
    private RecyclerView mRvCart;
    private TextView mTvTransactionDate;
    private TextView mTvSubTotal;
    private TextView mTvSellerName;
    private TextView mTvMemberName;
    private TextView mTvPointsRedeemed;
    private TextView mTvPointsAwarded;
    private TextView mTvDiscount;
    private TextView mTvTotal;
    private TextView mTvNoMemberAdded;
    private ProgressBar mPbLoading;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Assign the reference to the view components
        mRvCart = getView().findViewById(R.id.rvCart);
        mTvTransactionDate = getView().findViewById(R.id.tvTransactionDate);
        mTvSubTotal = getView().findViewById(R.id.tvSubtotal);
        mTvMemberName = getView().findViewById(R.id.tvMemberName);
        mTvSellerName = getView().findViewById(R.id.tvSellerName);
        mTvPointsRedeemed = getView().findViewById(R.id.tvPointsRedeemed);
        mTvPointsAwarded = getView().findViewById(R.id.tvPointsAwarded);
        mTvDiscount = getView().findViewById(R.id.tvDiscount);
        mTvTotal = getView().findViewById(R.id.tvTotal);
        mTvNoMemberAdded = getView().findViewById(R.id.tvNoMemberAdded);
        mPbLoading = getView().findViewById(R.id.pbLoading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get the transaction index from the fragment argument
        mIndex = getArguments().getInt(TransactionFragment.TRANSACTION_INDEX_FRAGMENT_ARG);

        // Get the ViewModel from the ViewModelProviders and specify the data persistence scope
        mTransactionViewModel = ViewModelProviders.of(getActivity()).get(TransactionViewModel.class);
        mTransactionDetailsAdapter = new TransactionDetailsAdapter(getActivity(), mTransactionViewModel, mIndex);

        // Set the data from the transaction object
        setData(mTransactionViewModel.getTransactions().getValue().get(mIndex));

        // Observe if the transaction was removed
        mTransactionViewModel.getTransactionIndexDeleted().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer != -1 && integer == mIndex) {

                    // If removed navigate back to the Transaction fragment
                    Toast.makeText(getActivity(), getString(R.string.transaction_deleted), Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStack();
                }
            }
        });

        // Observe if the transaction was loaded
        mTransactionViewModel.getTransactionLoadState().observe(getViewLifecycleOwner(), new Observer<TransactionLoadState>() {
            @Override
            public void onChanged(TransactionLoadState transactionLoadState) {
                if (transactionLoadState.equals(TransactionLoadState.NO_TRANSACTION)) {

                    // Navigate back to the home fragment
                    Toast.makeText(getActivity(), getString(R.string.no_transaction_available), Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStack();
                }
            }
        });

        // Populate the Cart RecyclerView containing the Transaction Items by setting the adapter
        mRvCart.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvCart.setAdapter(mTransactionDetailsAdapter);
    }

    /**
     * Set the transaction details
     * @param transaction transaction object containing the data to be set
     */
    public void setData(Transaction transaction) {

        // Update the view components
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        mTvTransactionDate.setText(simpleDateFormat.format(new Date(transaction.getTimestamp())));
        mTvSellerName.setText(transaction.getStoreName());

        mTvSubTotal.setText(getString(R.string.tvSubtotal, transaction.getSubtotal()));

        // If a member was added to the transaction, show the membership details
        if (transaction.getUserName() != null && transaction.getPointsAwarded() != null && transaction.getPointsRedeemed() != null) {
            mTvNoMemberAdded.setVisibility(View.GONE);

            mTvMemberName.setVisibility(View.VISIBLE);
            mTvPointsAwarded.setVisibility(View.VISIBLE);
            mTvPointsRedeemed.setVisibility(View.VISIBLE);

            mTvMemberName.setText(transaction.getUserName());
            mTvPointsAwarded.setText(getString(R.string.tvPointsAwarded, transaction.getPointsAwarded()));
            mTvPointsRedeemed.setText(getString(R.string.tvPointsRedeemed, transaction.getPointsRedeemed()));

        } else {

            // Hide the membership details
            mTvNoMemberAdded.setVisibility(View.VISIBLE);

            mTvMemberName.setVisibility(View.GONE);
            mTvPointsAwarded.setVisibility(View.GONE);
            mTvPointsRedeemed.setVisibility(View.GONE);
        }

        mTvDiscount.setText(getString(R.string.tvDiscount, transaction.getDiscount()));
        mTvTotal.setText(getString(R.string.tvTotal, transaction.getTotal()));
    }
}
