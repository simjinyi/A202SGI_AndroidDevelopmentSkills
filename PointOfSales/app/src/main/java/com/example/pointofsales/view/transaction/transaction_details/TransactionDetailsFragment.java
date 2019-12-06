package com.example.pointofsales.view.transaction.transaction_details;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
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
import com.example.pointofsales.utility.LoadingScreen;
import com.example.pointofsales.view.product.ProductFragment;
import com.example.pointofsales.view.transaction.TransactionAdapter;
import com.example.pointofsales.view.transaction.TransactionDecoration;
import com.example.pointofsales.view.transaction.TransactionFragment;
import com.example.pointofsales.view.transaction.ViewDetailsButtonClick;
import com.example.pointofsales.viewmodel.TransactionViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransactionDetailsFragment extends Fragment {

    private int mIndex;

    private TransactionViewModel mTransactionViewModel;
    private TransactionDetailsAdapter mTransactionDetailsAdapter;

    private RecyclerView mRvCart;
    private TextView mTvTransactionDate;
    private TextView mTvSubTotal;
    private TextView mTvMemberName;
    private TextView mTvPointsRedeemed;
    private TextView mTvPointsAwarded;
    private TextView mTvDiscount;
    private TextView mTvTotal;
    private TextView mTvNoMemberAdded;
    private ProgressBar mPbLoading;

    private LoadingScreen mLoadingScreen;

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
        mTvPointsRedeemed = getView().findViewById(R.id.tvPointsRedeemed);
        mTvPointsAwarded = getView().findViewById(R.id.tvPointsAwarded);
        mTvDiscount = getView().findViewById(R.id.tvDiscount);
        mTvTotal = getView().findViewById(R.id.tvTotal);
        mTvNoMemberAdded = getView().findViewById(R.id.tvNoMemberAdded);
        mPbLoading = getView().findViewById(R.id.pbLoading);

        mLoadingScreen = new LoadingScreen(getActivity(), mPbLoading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mIndex = getArguments().getInt(TransactionFragment.TRANSACTION_INDEX_FRAGMENT_ARG);
        mTransactionViewModel = ViewModelProviders.of(getActivity()).get(TransactionViewModel.class);
        mTransactionDetailsAdapter = new TransactionDetailsAdapter(getActivity(), mTransactionViewModel, mIndex);

        setData(mTransactionViewModel.getTransactions().getValue().get(mIndex));

        mTransactionViewModel.getTransactions().observe(getViewLifecycleOwner(), new Observer<ArrayList<Transaction>>() {
            @Override
            public void onChanged(ArrayList<Transaction> transactions) {
                setData(transactions.get(mIndex));
                mTransactionDetailsAdapter.notifyDataSetChanged();
            }
        });

        mRvCart.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvCart.setAdapter(mTransactionDetailsAdapter);
    }

    public void setData(Transaction transaction) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        mTvTransactionDate.setText(simpleDateFormat.format(new Date(transaction.getTimestamp())));

        mTvSubTotal.setText(getString(R.string.tvSubtotal, transaction.getSubtotal()));

        if (transaction.getUserName() != null && transaction.getPointsAwarded() != null && transaction.getPointsRedeemed() != null) {
            mTvNoMemberAdded.setVisibility(View.GONE);

            mTvMemberName.setVisibility(View.VISIBLE);
            mTvPointsAwarded.setVisibility(View.VISIBLE);
            mTvPointsRedeemed.setVisibility(View.VISIBLE);

            mTvMemberName.setText(transaction.getUserName());
            mTvPointsAwarded.setText(getString(R.string.tvPointsAwarded, transaction.getPointsAwarded()));
            mTvPointsRedeemed.setText(getString(R.string.tvPointsAwarded, transaction.getPointsAwarded()));

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
