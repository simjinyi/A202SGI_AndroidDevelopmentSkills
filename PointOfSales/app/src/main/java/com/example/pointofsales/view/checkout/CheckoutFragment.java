package com.example.pointofsales.view.checkout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.model.state.CartOpenableState;
import com.example.pointofsales.model.state.CartRemovalState;
import com.example.pointofsales.model.state.ProductInventoryQuantityChangeState;
import com.example.pointofsales.viewmodel.ProductViewModel;
import com.example.pointofsales.viewmodel.ProductViewModelFactory;
import com.example.pointofsales.viewmodel.UserViewModel;

import java.util.ArrayList;

public class CheckoutFragment extends Fragment {

    private ProductViewModel mProductViewModel;
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

        mProductViewModel = ViewModelProviders.of(getActivity()).get(ProductViewModel.class);
        mCartAdapter = new CartAdapter(getActivity(), mProductViewModel);
        mCartAdapter.setHasStableIds(true);

        rvCart.setLayoutManager(new CartLinearLayoutManager(getActivity()));
        rvCart.setAdapter(mCartAdapter);

        mProductViewModel.getCartList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Product>>() {
            @Override
            public void onChanged(ArrayList<Product> products) {
                mCartAdapter.notifyDataSetChanged();
            }
        });

        mProductViewModel.getCartOpenableState().observe(getViewLifecycleOwner(), new Observer<CartOpenableState>() {
            @Override
            public void onChanged(CartOpenableState cartOpenableState) {
                if (cartOpenableState.equals(CartOpenableState.DISABLED))
                    getFragmentManager().popBackStack();
            }
        });

        mProductViewModel.getProductInventoryQuantityChangeState().observe(getViewLifecycleOwner(), new Observer<ProductInventoryQuantityChangeState>() {
            @Override
            public void onChanged(ProductInventoryQuantityChangeState productInventoryQuantityChangeState) {
                if (productInventoryQuantityChangeState.getProductNames().size() > 0) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.product_quantity_changed))
                            .setMessage(getString(R.string.product_quantity_changed_description, productInventoryQuantityChangeState.toString()))
                            .setIcon(R.drawable.ic_info_24dp)
                            .setCancelable(false)
                            .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    mProductViewModel.clearProductInventoryQuantityChangeFlag();
                }
            }
        });

        mProductViewModel.getCartRemovalState().observe(getViewLifecycleOwner(), new Observer<CartRemovalState>() {
            @Override
            public void onChanged(CartRemovalState cartRemovalState) {
                if (cartRemovalState.getProductNames().size() > 0) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.product_removed_from_cart))
                            .setMessage(getString(R.string.product_removed_from_cart_description, cartRemovalState.toString()))
                            .setIcon(R.drawable.ic_info_24dp)
                            .setCancelable(false)
                            .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    mProductViewModel.clearCartRemovalFlag();
                }
            }
        });
    }
}
