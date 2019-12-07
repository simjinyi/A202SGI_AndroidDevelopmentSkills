package com.example.pointofsales.view.checkout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Cart;
import com.example.pointofsales.model.Point;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.model.state.CartOpenableState;
import com.example.pointofsales.model.state.CartRemovalState;
import com.example.pointofsales.model.state.ProductInventoryQuantityChangeState;
import com.example.pointofsales.utility.LoadingScreen;
import com.example.pointofsales.view.OnSingleClickListener;
import com.example.pointofsales.viewmodel.CheckoutViewModel;
import com.example.pointofsales.viewmodel.CheckoutViewModelFactory;
import com.example.pointofsales.viewmodel.ProductViewModel;

import java.util.ArrayList;

/**
 * CheckoutFragment to handle the Checkout View
 */
public class CheckoutFragment extends Fragment {

    // ViewModel objects
    private ProductViewModel mProductViewModel;
    private CheckoutViewModel mCheckoutViewModel;

    // CartAdapter attribute
    private CartAdapter mCartAdapter;

    // View components
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
    private ProgressBar mPbLoading;

    private LoadingScreen mLoadingScreen;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Assign the reference to the view components
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
        mPbLoading = getView().findViewById(R.id.pbLoading);

        mLoadingScreen = new LoadingScreen(getActivity(), mPbLoading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get the ViewModels from the ViewModelProviders and specify the persistence scope
        mProductViewModel = ViewModelProviders.of(getActivity()).get(ProductViewModel.class);
        mCheckoutViewModel = ViewModelProviders.of(getActivity(), new CheckoutViewModelFactory(mProductViewModel)).get(CheckoutViewModel.class);
        mCartAdapter = new CartAdapter(getActivity(), mProductViewModel, mCheckoutViewModel);
        mCartAdapter.setHasStableIds(true);

        // Go back to the previous fragment
        mBtnCancel.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        // Scan the member QR Code to register the membership
        mBtnMember.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_checkout_to_navigation_scan);
            }
        });

        // Checkout the transaction
        mBtnSubmit.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                // Confirm that the seller wants to checkout
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.checkout_confirmation))
                        .setMessage(getString(R.string.checkout_description))
                        .setIcon(R.drawable.ic_warning_24dp)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Prompt a confirmation when the seller attempts to checkout without adding a member
                                if (mCheckoutViewModel.getPointsRedeemedAndAwarded().getValue() == null) {
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle(getString(R.string.checkout_no_member_confirmation))
                                            .setMessage(getString(R.string.checkout_no_member_description))
                                            .setIcon(R.drawable.ic_warning_24dp)
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    // Checkout
                                                    mCheckoutViewModel.checkout();
                                                    dialog.dismiss();
                                                }
                                            })
                                            .setNegativeButton(android.R.string.no, null).show();
                                } else {

                                    // Checkout
                                    mCheckoutViewModel.checkout();
                                    dialog.dismiss();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        mRvCart.setLayoutManager(new CartLinearLayoutManager(getActivity()));
        mRvCart.setAdapter(mCartAdapter);

        // Observe the changes made to the cart list
        mProductViewModel.getCartList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Product>>() {
            @Override
            public void onChanged(ArrayList<Product> products) {

                // Notify the changes on the cart adapter and update the point redeemed
                mCartAdapter.notifyDataSetChanged();
                mCheckoutViewModel.updatePoint(null, false);
            }
        });

        // Observe if the cart can be opened (at least one product exists in the cart)
        mProductViewModel.getCartOpenableState().observe(getViewLifecycleOwner(), new Observer<CartOpenableState>() {
            @Override
            public void onChanged(CartOpenableState cartOpenableState) {

                // If not openable navigate back to home
                if (cartOpenableState.equals(CartOpenableState.DISABLED) && mCheckoutViewModel.getCheckoutLoading().getValue().equals(false)) {
                    mCheckoutViewModel.clearPoint();
                    getFragmentManager().popBackStack();
                }
            }
        });

        // Observes and check if the cart quantity changed due to a change in the product inventory quantity
        mProductViewModel.getProductInventoryQuantityChangeState().observe(getViewLifecycleOwner(), new Observer<ProductInventoryQuantityChangeState>() {
            @Override
            public void onChanged(ProductInventoryQuantityChangeState productInventoryQuantityChangeState) {
                if (productInventoryQuantityChangeState.getProductNames().size() > 0) {

                    // Prompt a notification on the cart changed
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

        // Observes and check if the cart product removed due to product removal
        mProductViewModel.getCartRemovalState().observe(getViewLifecycleOwner(), new Observer<CartRemovalState>() {
            @Override
            public void onChanged(CartRemovalState cartRemovalState) {
                if (cartRemovalState.getProductNames().size() > 0) {

                    // Prompt a notification on the cart removal
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

        // Observe and update the cart details
        mProductViewModel.getCart().observe(getViewLifecycleOwner(), new Observer<Cart>() {
            @Override
            public void onChanged(Cart cart) {
                mTvSubTotal.setText(getString(R.string.tvSubtotal, cart.getSubtotal()));
                mTvDiscount.setText(getString(R.string.tvDiscount, cart.getDiscount()));
                mTvTotal.setText(getString(R.string.tvTotal, cart.getTotal()));
            }
        });

        // Observe the changes on the membership point
        mCheckoutViewModel.getPoint().observe(getViewLifecycleOwner(), new Observer<Point>() {
            @Override
            public void onChanged(Point point) {

                // Update the checkout view based on the membership
                if (point != null) {

                    mTvMemberName.setVisibility(View.VISIBLE);
                    mTvPointsRedeemed.setVisibility(View.VISIBLE);
                    mTvPointsAwarded.setVisibility(View.VISIBLE);
                    mTvNoMemberAdded.setVisibility(View.GONE);

                    mTvMemberName.setText(point.getUserName());
                    mTvPointsRedeemed.setText(getString(R.string.tvPointsRedeemed, mCheckoutViewModel.getPointsRedeemedAndAwarded().getValue().getRedeemedPoint()));
                    mTvPointsAwarded.setText(getString(R.string.tvPointsAwarded, mCheckoutViewModel.getPointsRedeemedAndAwarded().getValue().getPointAwarded()));

                } else {

                    mTvMemberName.setVisibility(View.GONE);
                    mTvPointsRedeemed.setVisibility(View.GONE);
                    mTvPointsAwarded.setVisibility(View.GONE);
                    mTvNoMemberAdded.setVisibility(View.VISIBLE);
                }
            }
        });

        // Observe the changes on the membership
        mCheckoutViewModel.getMemberChangedState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {

                    // If the membership points was changed due to a change in the cart or the membership point itself
                    // Prompt a notification
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

        // Observe the loading state of the checkout
        mCheckoutViewModel.getCheckoutLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean)
                    mLoadingScreen.start();
                else
                    mLoadingScreen.end();
            }
        });

        // Observe if the checkout process was completed
        mCheckoutViewModel.getCheckoutDone().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {

                    // Navigate back to home
                    Toast.makeText(getActivity(), getString(R.string.transaction_saved_successfully), Toast.LENGTH_SHORT).show();
                    mCheckoutViewModel.clearCheckoutDone();
                    getFragmentManager().popBackStack();
                }
            }
        });
    }
}
