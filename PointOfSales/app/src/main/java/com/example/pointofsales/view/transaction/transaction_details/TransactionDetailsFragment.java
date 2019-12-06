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

public class TransactionDetailsFragment extends Fragment {

    private int mIndex;

    private TransactionViewModel mTransactionViewModel;
    private TransactionDetailsAdapter mTransactionDetailsAdapter;

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

        mIndex = getArguments().getInt(TransactionFragment.TRANSACTION_INDEX_FRAGMENT_ARG);
        mTransactionViewModel = ViewModelProviders.of(getActivity()).get(TransactionViewModel.class);
        mTransactionDetailsAdapter = new TransactionDetailsAdapter(getActivity(), mTransactionViewModel, mIndex);

        setData(mTransactionViewModel.getTransactions().getValue().get(mIndex));

        mTransactionViewModel.getTransactionIndexDeleted().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer != -1 && integer == mIndex) {
                    Toast.makeText(getActivity(), getString(R.string.transaction_deleted), Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStack();
                }
            }
        });

        mTransactionViewModel.getTransactionLoadState().observe(getViewLifecycleOwner(), new Observer<TransactionLoadState>() {
            @Override
            public void onChanged(TransactionLoadState transactionLoadState) {
                if (transactionLoadState.equals(TransactionLoadState.NO_TRANSACTION)) {
                    Toast.makeText(getActivity(), getString(R.string.no_transaction_available), Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStack();
                }
            }
        });

        mRvCart.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvCart.setAdapter(mTransactionDetailsAdapter);
    }

    public void setData(Transaction transaction) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        mTvTransactionDate.setText(simpleDateFormat.format(new Date(transaction.getTimestamp())));
        mTvSellerName.setText(transaction.getStoreName());

        mTvSubTotal.setText(getString(R.string.tvSubtotal, transaction.getSubtotal()));

        if (transaction.getUserName() != null && transaction.getPointsAwarded() != null && transaction.getPointsRedeemed() != null) {
            mTvNoMemberAdded.setVisibility(View.GONE);

            mTvMemberName.setVisibility(View.VISIBLE);
            mTvPointsAwarded.setVisibility(View.VISIBLE);
            mTvPointsRedeemed.setVisibility(View.VISIBLE);

            mTvMemberName.setText(transaction.getUserName());
            mTvPointsAwarded.setText(getString(R.string.tvPointsAwarded, transaction.getPointsAwarded()));
            mTvPointsRedeemed.setText(getString(R.string.tvPointsRedeemed, transaction.getPointsRedeemed()));

        } else {

            mTvNoMemberAdded.setVisibility(View.VISIBLE);

            mTvMemberName.setVisibility(View.GONE);
            mTvPointsAwarded.setVisibility(View.GONE);
            mTvPointsRedeemed.setVisibility(View.GONE);
        }

        mTvDiscount.setText(getString(R.string.tvDiscount, transaction.getDiscount()));
        mTvTotal.setText(getString(R.string.tvTotal, transaction.getTotal()));
    }
}
