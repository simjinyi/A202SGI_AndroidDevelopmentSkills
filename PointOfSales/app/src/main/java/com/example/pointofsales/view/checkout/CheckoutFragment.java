package com.example.pointofsales.view.checkout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Cart;
import com.example.pointofsales.model.Point;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.model.state.CartOpenableState;
import com.example.pointofsales.model.state.CartRemovalState;
import com.example.pointofsales.model.state.ProductInventoryQuantityChangeState;
import com.example.pointofsales.view.OnSingleClickListener;
import com.example.pointofsales.viewmodel.CheckoutViewModel;
import com.example.pointofsales.viewmodel.CheckoutViewModelFactory;
import com.example.pointofsales.viewmodel.ProductViewModel;

import java.util.ArrayList;

public class CheckoutFragment extends Fragment {

    private ProductViewModel mProductViewModel;
    private CheckoutViewModel mCheckoutViewModel;
    private CartAdapter mCartAdapter;

    private RecyclerView mRvCart;
    private TextView mTvSubTotal;
    private TextView mTvMemberName;
    private TextView mTvPointsRedeemed;
    private TextView mTvPointsAwarded;
    private TextView mTvDiscount;
    private TextView mTvTotal;
    private TextView mTvNoMemberAdded;
    private Button mBtnCancel;
    private Button mBtnMember;
    private Button mBtnSubmit;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRvCart = getView().findViewById(R.id.rvCart);
        mTvSubTotal = getView().findViewById(R.id.tvSubtotal);
        mTvMemberName = getView().findViewById(R.id.tvMemberName);
        mTvPointsRedeemed = getView().findViewById(R.id.tvPointsRedeemed);
        mTvPointsAwarded = getView().findViewById(R.id.tvPointsAwarded);
        mTvDiscount = getView().findViewById(R.id.tvDiscount);
        mTvTotal = getView().findViewById(R.id.tvTotal);
        mTvNoMemberAdded = getView().findViewById(R.id.tvNoMemberAdded);
        mBtnCancel = getView().findViewById(R.id.btnCancel);
        mBtnMember = getView().findViewById(R.id.btnMember);
        mBtnSubmit = getView().findViewById(R.id.btnSubmit);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mProductViewModel = ViewModelProviders.of(getActivity()).get(ProductViewModel.class);
        mCheckoutViewModel = ViewModelProviders.of(getActivity(), new CheckoutViewModelFactory(mProductViewModel)).get(CheckoutViewModel.class);
        mCartAdapter = new CartAdapter(getActivity(), mProductViewModel, mCheckoutViewModel);

        mCartAdapter.setHasStableIds(true);

        mBtnCancel.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        mBtnMember.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_checkout_to_navigation_scan);
            }
        });

        mBtnSubmit.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

            }
        });

        mRvCart.setLayoutManager(new CartLinearLayoutManager(getActivity()));
        mRvCart.setAdapter(mCartAdapter);

        mProductViewModel.getCartList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Product>>() {
            @Override
            public void onChanged(ArrayList<Product> products) {
                mCartAdapter.notifyDataSetChanged();
                mCheckoutViewModel.updatePoint(null);
            }
        });

        mProductViewModel.getCartOpenableState().observe(getViewLifecycleOwner(), new Observer<CartOpenableState>() {
            @Override
            public void onChanged(CartOpenableState cartOpenableState) {
                if (cartOpenableState.equals(CartOpenableState.DISABLED)) {
                    mCheckoutViewModel.clearPoint();
                    getFragmentManager().popBackStack();
                }
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

        mProductViewModel.getCart().observe(getViewLifecycleOwner(), new Observer<Cart>() {
            @Override
            public void onChanged(Cart cart) {
                mTvSubTotal.setText(getString(R.string.tvSubtotal, cart.getSubtotal()));
                mTvDiscount.setText(getString(R.string.tvDiscount, cart.getDiscount()));
                mTvTotal.setText(getString(R.string.tvTotal, cart.getTotal()));
            }
        });

        mCheckoutViewModel.getPoint().observe(getViewLifecycleOwner(), new Observer<Point>() {
            @Override
            public void onChanged(Point point) {
                if (point != null) {
                    mTvMemberName.setText(point.getUserName());
                }
            }
        });

        mCheckoutViewModel.getMemberChangedState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.member_point_changed))
                            .setMessage(getString(R.string.member_point_changed_description))
                            .setIcon(R.drawable.ic_info_24dp)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    mCheckoutViewModel.clearMemberPointChangedState();
                }
            }
        });
    }
}
