package com.example.pointofsales.view.transaction;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Transaction;
import com.example.pointofsales.model.UserType;
import com.example.pointofsales.model.state.TransactionLoadState;
import com.example.pointofsales.utility.LoadingScreen;
import com.example.pointofsales.viewmodel.TransactionViewModel;
import com.example.pointofsales.viewmodel.UserViewModel;

import java.util.ArrayList;

/**
 * TransactionFragment handles the transaction page
 */
public class TransactionFragment extends Fragment implements ViewDetailsButtonClick {

    // Constant for the transaction index fragment argument
    public static final String TRANSACTION_INDEX_FRAGMENT_ARG = "com.example.pointofsales.view.transaction.TRANSACTION_ID_FRAGMENT_ARG";

    // ViewModel
    private TransactionViewModel mTransactionViewModel;
    private TransactionAdapter mTransactionAdapter;

    // View components
    private TextView mTvSellerHeader;
    private TextView mTvCustomerHeader;
    private RecyclerView mRvTransaction;
    private TextView mTvTotalTransaction;
    private CardView mCvNoTransaction;
    private ProgressBar mPbLoading;

    private LoadingScreen mLoadingScreen;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Assign reference to the View components
        mTvSellerHeader = getView().findViewById(R.id.tvSellerHeader);
        mTvCustomerHeader = getView().findViewById(R.id.tvCustomerHeader);
        mRvTransaction = getView().findViewById(R.id.rvTransaction);
        mTvTotalTransaction = getView().findViewById(R.id.tvTotalTransaction);
        mCvNoTransaction = getView().findViewById(R.id.cvNoTransaction);
        mPbLoading = getView().findViewById(R.id.pbLoading);

        mLoadingScreen = new LoadingScreen(getActivity(), mPbLoading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get the ViewModel
        mTransactionViewModel = ViewModelProviders.of(getActivity()).get(TransactionViewModel.class);

        // Set the transaction adapter
        // Populate the RecyclerView by setting the transaction adapter
        mTransactionAdapter = new TransactionAdapter(getActivity(), mTransactionViewModel, this);
        mTransactionAdapter.setHasStableIds(true);
        mRvTransaction.addItemDecoration(new TransactionDecoration(getResources().getDimension(R.dimen.default_dimen), mTransactionViewModel));
        mRvTransaction.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvTransaction.setAdapter(mTransactionAdapter);

        // Observes the changes to the list of transactions
        mTransactionViewModel.getTransactions().observe(getViewLifecycleOwner(), new Observer<ArrayList<Transaction>>() {
            @Override
            public void onChanged(ArrayList<Transaction> transactions) {

                // Notify the dataset changed and calculate the total transaction
                mTransactionAdapter.notifyDataSetChanged();
                mTransactionViewModel.calculateTotalTransaction(transactions);
            }
        });

        // Observes the transaction load state
        mTransactionViewModel.getTransactionLoadState().observe(getViewLifecycleOwner(), new Observer<TransactionLoadState>() {
            @Override
            public void onChanged(TransactionLoadState transactionLoadState) {

                // Update the view header visibility
                if (UserViewModel.getUser().getType().equals(UserType.SELLER)) {
                    mTvSellerHeader.setVisibility(View.GONE);
                    mTvCustomerHeader.setVisibility(View.VISIBLE);
                } else {
                    mTvSellerHeader.setVisibility(View.VISIBLE);
                    mTvCustomerHeader.setVisibility(View.GONE);
                }

                // Update the view according to the transaction load state
                switch (transactionLoadState) {
                    case LOADED:
                        mLoadingScreen.end();
                        mCvNoTransaction.setVisibility(View.GONE);
                        break;

                    case NO_TRANSACTION:
                        mCvNoTransaction.setVisibility(View.VISIBLE);
                        mLoadingScreen.end();
                        break;

                    default:
                        mLoadingScreen.start();
                }
            }
        });

        // Observe the changes in the total transaction
        mTransactionViewModel.getTotalTransaction().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                // Update the total transaction
                mTvTotalTransaction.setText(getString(R.string.tvTotalTransaction, aFloat));
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.seach_sort_menu, menu);

        // Handle the actionbar search feature
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.transaction_query_hint));

        // Apply the filter
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

            // Sort the transaction and prompt the message on the way the items were sorted
            String sortText = getString(mTransactionViewModel.sort());
            item.setTitle(sortText);
            Toast.makeText(getActivity(), getString(R.string.sorting_by, sortText), Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onViewDetailsButtonClick(Transaction transaction) {

        // Navigate to the transaction details page with the transaction index in the dataset passed as the fragment argument
        Bundle bundle = new Bundle();
        bundle.putInt(TRANSACTION_INDEX_FRAGMENT_ARG, mTransactionViewModel.getTransactionIndexFromTransactionId(transaction.getTransactionId()));
        Navigation.findNavController(getView()).navigate(R.id.action_navigation_transaction_to_navigation_transaction_details, bundle);
    }
}
