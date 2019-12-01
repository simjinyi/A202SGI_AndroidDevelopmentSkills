package com.example.pointofsales.view.checkout;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Cart;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.viewmodel.CheckoutViewModel;
import com.example.pointofsales.viewmodel.ProductViewModel;

import java.util.ArrayList;

public class CheckoutFragment extends Fragment {

    private CheckoutViewModel mCheckoutViewModel;
    private CartAdapter mCartAdapter;

    private RecyclerView rvCart;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvCart = getActivity().findViewById(R.id.rvCart);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCheckoutViewModel = ViewModelProviders.of(getActivity()).get(CheckoutViewModel.class);
        mCartAdapter = new CartAdapter(getActivity(), mCheckoutViewModel);

        rvCart.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvCart.setAdapter(mCartAdapter);

        mCheckoutViewModel.getCartItems().observe(this, new Observer<ArrayList<Cart>>() {
            @Override
            public void onChanged(ArrayList<Cart> carts) {
                mCartAdapter.notifyDataSetChanged();
            }
        });
    }
}
