package com.example.pointofsales.view.transaction;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Transaction;
import com.example.pointofsales.viewmodel.TransactionViewModel;

import java.util.ArrayList;

public class TransactionFragment extends Fragment implements ViewDetailsButtonClick {

    public static final String TRANSACTION_INDEX_FRAGMENT_ARG = "com.example.pointofsales.view.transaction.TRANSACTION_ID_FRAGMENT_ARG";

    private TransactionViewModel mTransactionViewModel;
    private TransactionAdapter mTransactionAdapter;

    private RecyclerView mRvTransaction;
    private TextView mTvTotalTransaction;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRvTransaction = getView().findViewById(R.id.rvTransaction);
        mTvTotalTransaction = getView().findViewById(R.id.tvTotalTransaction);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTransactionViewModel = ViewModelProviders.of(getActivity()).get(TransactionViewModel.class);

        mTransactionAdapter = new TransactionAdapter(getActivity(), mTransactionViewModel, this);
        mTransactionAdapter.setHasStableIds(true);
        mRvTransaction.addItemDecoration(new TransactionDecoration(getResources().getDimension(R.dimen.default_dimen), mTransactionViewModel));
        mRvTransaction.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvTransaction.setAdapter(mTransactionAdapter);

        mTransactionViewModel.getTransactions().observe(getViewLifecycleOwner(), new Observer<ArrayList<Transaction>>() {
            @Override
            public void onChanged(ArrayList<Transaction> transactions) {
                mTransactionAdapter.notifyDataSetChanged();
                mTransactionViewModel.calculateTotalTransaction(transactions);
            }
        });

        mTransactionViewModel.getTotalTransaction().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                mTvTotalTransaction.setText(getString(R.string.tvTotalTransaction, aFloat));
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.seach_sort_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.transaction_query_hint));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mTransactionAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mTransactionAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_bar_sort) {
            String sortText = getString(mTransactionViewModel.sort());
            item.setTitle(sortText);
            Toast.makeText(getActivity(), getString(R.string.sorting_by, sortText), Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewDetailsButtonClick(Transaction transaction) {
        Bundle bundle = new Bundle();
        bundle.putInt(TRANSACTION_INDEX_FRAGMENT_ARG, mTransactionViewModel.getTransactionIndexFromTransactionId(transaction.getTransactionId()));
        Navigation.findNavController(getView()).navigate(R.id.action_navigation_transaction_to_navigation_transaction_details, bundle);
    }
}
