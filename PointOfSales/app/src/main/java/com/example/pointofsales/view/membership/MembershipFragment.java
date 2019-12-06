package com.example.pointofsales.view.membership;

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

import com.example.pointofsales.viewmodel.MembershipViewModel;
import com.example.pointofsales.R;

public class MembershipFragment extends Fragment {

    private MembershipViewModel mViewModel;

    public static MembershipFragment newInstance() {
        return new MembershipFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_membership, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MembershipViewModel.class);

        RecyclerView rvMembership = getView().findViewById(R.id.rvMembership);
        rvMembership.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMembership.setAdapter(new MembershipAdapter(getActivity()));
    }

}
