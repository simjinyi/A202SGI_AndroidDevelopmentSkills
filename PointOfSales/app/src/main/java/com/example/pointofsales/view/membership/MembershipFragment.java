package com.example.pointofsales.view.membership;

import androidx.lifecycle.Observer;
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

import com.example.pointofsales.model.Point;
import com.example.pointofsales.viewmodel.MembershipViewModel;
import com.example.pointofsales.R;

import java.util.ArrayList;

public class MembershipFragment extends Fragment {

    private MembershipViewModel mMembershipViewModel;
    private MembershipAdapter mMembershipAdapter;

    private RecyclerView mRvMembership;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_membership, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRvMembership = getView().findViewById(R.id.rvMembership);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMembershipViewModel = ViewModelProviders.of(this).get(MembershipViewModel.class);
        mMembershipAdapter = new MembershipAdapter(getActivity(), mMembershipViewModel);

        mRvMembership.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRvMembership.setAdapter(mMembershipAdapter);

        mMembershipViewModel.getPoints().observe(getViewLifecycleOwner(), new Observer<ArrayList<Point>>() {
            @Override
            public void onChanged(ArrayList<Point> points) {
                mMembershipAdapter.notifyDataSetChanged();
            }
        });
    }

}
