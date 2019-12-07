package com.example.pointofsales.view.checkout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Cart;
import com.example.pointofsales.model.Point;
import com.example.pointofsales.model.state.CartOpenableState;
import com.example.pointofsales.model.state.CartRemovalState;
import com.example.pointofsales.model.state.ProductInventoryQuantityChangeState;
import com.example.pointofsales.utility.LoadingScreen;
import com.example.pointofsales.view.OnSingleClickListener;
import com.example.pointofsales.viewmodel.CheckoutViewModel;
import com.example.pointofsales.viewmodel.CheckoutViewModelFactory;
import com.example.pointofsales.viewmodel.ProductViewModel;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

/**
 * ScanFragment to add membership to a transaction
 */
public class ScanFragment extends Fragment {

    // View components
    private EditText mEtName;
    private EditText mEtAvailablePoints;
    private EditText mEtPointsRedeem;
    private EditText mEtSubtotal;
    private EditText mEtDiscount;
    private EditText mEtTotal;
    private ConstraintLayout mClNoMember;
    private Button mBtnScanQRCode;
    private Button mBtnSubmit;
    private Button mBtnClearMember;
    private ProgressBar mPbLoading;

    private LoadingScreen mLoadingScreen;

    // Scanner IntentIntegrator
    private IntentIntegrator mIntentIntegrator;

    // ViewModels
    private CheckoutViewModel mCheckoutViewModel;
    private ProductViewModel mProductViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Assign reference to the view components
        mEtName = getView().findViewById(R.id.etName);
        mEtAvailablePoints = getView().findViewById(R.id.etAvailablePoints);
        mEtPointsRedeem = getView().findViewById(R.id.etPointsRedeem);
        mEtSubtotal = getView().findViewById(R.id.etSubtotal);
        mEtDiscount = getView().findViewById(R.id.etDiscount);
        mEtTotal = getView().findViewById(R.id.etTotal);
        mClNoMember = getView().findViewById(R.id.clNoMember);
        mBtnScanQRCode = getView().findViewById(R.id.btnScanQRCode);
        mBtnSubmit = getView().findViewById(R.id.btnSubmit);
        mBtnClearMember = getView().findViewById(R.id.btnClearMember);
        mPbLoading = getView().findViewById(R.id.pbLoading);

        mLoadingScreen = new LoadingScreen(getActivity(), mPbLoading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get the ViewModels from the ViewModelProviders and specify the scope of data persistence
        mProductViewModel = ViewModelProviders.of(getActivity()).get(ProductViewModel.class);
        mCheckoutViewModel = ViewModelProviders.of(getActivity(), new CheckoutViewModelFactory(mProductViewModel)).get(CheckoutViewModel.class);

        // Form the IntentIntegrator for the ZXing Scanner Activity
        mIntentIntegrator = IntentIntegrator.forSupportFragment(this);
        mIntentIntegrator.setBeepEnabled(false);
        mIntentIntegrator.setOrientationLocked(false);
        mIntentIntegrator.setCaptureActivity(PortraitCaptureActivity.class);

        mCheckoutViewModel.updateEditText();

        // Check if the redeemed point enter is valid
        mCheckoutViewModel.getPointsRedeemedError().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer != null) {

                    // Prompt the error if the point is invalid
                    mBtnSubmit.setEnabled(false);
                    mEtPointsRedeem.setError(getString(R.string.point_redeem_error, integer));
                } else {
                    mBtnSubmit.setEnabled(true);
                }
            }
        });

        // Initiate the scanner activity
        mBtnScanQRCode.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mIntentIntegrator.initiateScan();
            }
        });

        // Observe if the membership was added to the transaction
        mCheckoutViewModel.getPoint().observe(getViewLifecycleOwner(), new Observer<Point>() {
            @Override
            public void onChanged(Point point) {

                // Assign the view based on the membership added
                if (point != null) {

                    mClNoMember.setVisibility(View.GONE);
                    mEtName.setText(point.getUserName());
                    mEtAvailablePoints.setText(String.valueOf(point.getPoints()));
                    mEtPointsRedeem.setEnabled(true);
                    mEtPointsRedeem.requestFocus();

                } else {

                    mClNoMember.setVisibility(View.VISIBLE);
                    mEtName.getText().clear();
                    mEtAvailablePoints.getText().clear();
                    mEtPointsRedeem.setEnabled(false);
                    mEtPointsRedeem.getText().clear();
                }

                mLoadingScreen.end();
            }
        });

        // Observe the points redeemed, and set the value in the EditText
        mCheckoutViewModel.getEditTextValue().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mEtPointsRedeem.setText(String.valueOf(integer));
            }
        });

        // Observe if the QR Code scanned was invalid
        mCheckoutViewModel.getScanUserNotFound().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {

                    // Prompt an error on the invalid user (not found in the database)
                    Toast.makeText(getActivity(), getString(R.string.user_not_found), Toast.LENGTH_SHORT).show();
                    mCheckoutViewModel.clearScanUserNotFoundFlag();
                }

                mLoadingScreen.end();
            }
        });

        // Observe the changes on the cart details and update the view accordingly
        mProductViewModel.getCart().observe(getViewLifecycleOwner(), new Observer<Cart>() {
            @Override
            public void onChanged(Cart cart) {
                mEtSubtotal.setText(getString(R.string.tvSubtotal, cart.getSubtotal()));
                mEtDiscount.setText(getString(R.string.tvDiscount, cart.getDiscount()));
                mEtTotal.setText(getString(R.string.tvTotal, cart.getTotal()));
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

        // Observe if the cart can be opened (at least one product exists in the cart)
        mProductViewModel.getCartOpenableState().observe(getViewLifecycleOwner(), new Observer<CartOpenableState>() {
            @Override
            public void onChanged(CartOpenableState cartOpenableState) {

                // If not openable navigate back to home
                if (cartOpenableState.equals(CartOpenableState.DISABLED)) {
                    mCheckoutViewModel.clearPoint();
                    getFragmentManager().popBackStack();
                }
            }
        });

        // Clear the added membership
        mBtnClearMember.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                // Prompt a confirmation
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.clear_member))
                        .setMessage(getString(R.string.clear_member_description))
                        .setIcon(R.drawable.ic_warning_24dp)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Clear the membership and navigate back to the checkout fragment
                                mCheckoutViewModel.clearPoint();
                                Toast.makeText(getActivity(), getResources().getString(R.string.member_cleared_successfully), Toast.LENGTH_SHORT).show();
                                getFragmentManager().popBackStack();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        // Validate if the points to redeem entered by the seller is valid
        mEtPointsRedeem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                mCheckoutViewModel.redeemPointChanged(mEtPointsRedeem.getText().toString());
            }
        });

        // Save and goes back to the checkout fragment
        mBtnSubmit.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        // Check if the result is valid
        if (resultCode == Activity.RESULT_OK) {

            // Get the result
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (result != null) {

                // Assign the membership based on the UserId scanned
                mCheckoutViewModel.assignPoint(result.getContents());
                mLoadingScreen.start();
            }
        }
    }
}
