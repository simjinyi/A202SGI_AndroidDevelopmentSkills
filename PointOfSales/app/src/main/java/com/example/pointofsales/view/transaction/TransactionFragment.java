package com.example.pointofsales.view.transaction;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pointofsales.R;
import com.example.pointofsales.viewmodel.TransactionViewModel;

public class TransactionFragment extends Fragment {

    private TransactionViewModel mTransactionViewModel;

    private RecyclerView mRvTransaction;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRvTransaction = getView().findViewById(R.id.rvTransaction);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTransactionViewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);

        mRvTransaction.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvTransaction.setAdapter(new TransactionAdapter(getActivity(), mTransactionViewModel));
    }

}
