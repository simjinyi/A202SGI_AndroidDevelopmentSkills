package com.example.pointofsales.view.membership;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Point;
import com.example.pointofsales.model.state.PointLoadState;
import com.example.pointofsales.utility.LoadingScreen;
import com.example.pointofsales.viewmodel.MembershipViewModel;

import java.util.ArrayList;

/**
 * MembershipFragment to handle the membership page
 */
public class MembershipFragment extends Fragment {

    private MembershipViewModel mMembershipViewModel;
    private MembershipAdapter mMembershipAdapter;

    // View components
    private RecyclerView mRvMembership;
    private CardView mCvNoMembership;
    private ProgressBar mPbLoading;

    private LoadingScreen mLoadingScreen;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_membership, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Assign the reference to the view components
        mRvMembership = getView().findViewById(R.id.rvMembership);
        mCvNoMembership = getView().findViewById(R.id.cvNoMembership);
        mPbLoading = getView().findViewById(R.id.pbLoading);

        mLoadingScreen = new LoadingScreen(getActivity(), mPbLoading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get the ViewModel
        mMembershipViewModel = ViewModelProviders.of(getActivity()).get(MembershipViewModel.class);
        mMembershipAdapter = new MembershipAdapter(getActivity(), mMembershipViewModel);
        mMembershipAdapter.setHasStableIds(true);

        // Populate the RecyclerView by assigning the adapter
        mRvMembership.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvMembership.addItemDecoration(new MembershipDecoration(getResources().getDimension(R.dimen.default_dimen), mMembershipViewModel));
        mRvMembership.setAdapter(mMembershipAdapter);

        // Observe the changes on the ArrayList of Point object (Membership)
        mMembershipViewModel.getPoints().observe(getViewLifecycleOwner(), new Observer<ArrayList<Point>>() {
            @Override
            public void onChanged(ArrayList<Point> points) {
                // Update the RecyclerView
                mMembershipAdapter.notifyDataSetChanged();
            }
        });

        // Observe if the membership was currenly being loaded from the database
        mMembershipViewModel.getPointLoadState().observe(getViewLifecycleOwner(), new Observer<PointLoadState>() {
            @Override
            public void onChanged(PointLoadState pointLoadState) {

                // Update the view accordingly
                switch (pointLoadState) {
                    case LOADED:
                        mLoadingScreen.end();
                        mCvNoMembership.setVisibility(View.GONE);
                        break;

                    case NO_POINT:
                        mLoadingScreen.end();
                        mCvNoMembership.setVisibility(View.VISIBLE);
                        break;

                    default:
                        mLoadingScreen.start();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.seach_sort_menu, menu);

        // Handle the actionbar search feature
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.membership_query_hint));

        // Apply the filter
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mMembershipAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mMembershipAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_bar_sort) {

            // Sort the membership and prompt the message on the way the items were sorted
            String sortText = getString(mMembershipViewModel.sort());
            item.setTitle(sortText);
            Toast.makeText(getActivity(), getString(R.string.sorting_by, sortText), Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
