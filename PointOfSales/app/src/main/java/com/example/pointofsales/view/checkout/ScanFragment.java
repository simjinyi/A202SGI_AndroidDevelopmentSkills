package com.example.pointofsales.view.checkout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Cart;
import com.example.pointofsales.model.Point;
import com.example.pointofsales.model.state.CartRemovalState;
import com.example.pointofsales.model.state.ProductInventoryQuantityChangeState;
import com.example.pointofsales.utility.LoadingScreen;
import com.example.pointofsales.view.OnSingleClickListener;
import com.example.pointofsales.viewmodel.CheckoutViewModel;
import com.example.pointofsales.viewmodel.CheckoutViewModelFactory;
import com.example.pointofsales.viewmodel.ProductViewModel;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanFragment extends Fragment {

    private EditText mEtName;
    private EditText mEtAvailablePoints;
    private EditText mEtPointsRedeem;
    private EditText mEtSubtotal;
    private EditText mEtDiscount;
    private EditText mEtTotal;
    private ConstraintLayout mClNoMember;
    private Button mBtnScanQRCode;
    private Button mBtnCancel;
    private Button mBtnSubmit;
    private Button mBtnClearMember;
    private ProgressBar mPbLoading;

    private LoadingScreen mLoadingScreen;

    private IntentIntegrator mIntentIntegrator;

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

        mEtName = getView().findViewById(R.id.etName);
        mEtAvailablePoints = getView().findViewById(R.id.etAvailablePoints);
        mEtPointsRedeem = getView().findViewById(R.id.etPointsRedeem);
        mEtSubtotal = getView().findViewById(R.id.etSubtotal);
        mEtDiscount = getView().findViewById(R.id.etDiscount);
        mEtTotal = getView().findViewById(R.id.etTotal);
        mClNoMember = getView().findViewById(R.id.clNoMember);
        mBtnScanQRCode = getView().findViewById(R.id.btnScanQRCode);
        mBtnCancel = getView().findViewById(R.id.btnCancel);
        mBtnSubmit = getView().findViewById(R.id.btnSubmit);
        mBtnClearMember = getView().findViewById(R.id.btnClearMember);
        mPbLoading = getView().findViewById(R.id.pbLoading);

        mLoadingScreen = new LoadingScreen(getActivity(), mPbLoading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mProductViewModel = ViewModelProviders.of(getActivity()).get(ProductViewModel.class);
        mCheckoutViewModel = ViewModelProviders.of(this, new CheckoutViewModelFactory(mProductViewModel)).get(CheckoutViewModel.class);

        mIntentIntegrator = IntentIntegrator.forSupportFragment(this);
        mIntentIntegrator.setBeepEnabled(false);
        mIntentIntegrator.setOrientationLocked(false);
        mIntentIntegrator.setCaptureActivity(PortraitCaptureActivity.class);

        mBtnScanQRCode.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                mIntentIntegrator.initiateScan();
            }
        });

        mCheckoutViewModel.getPoint().observe(getViewLifecycleOwner(), new Observer<Point>() {
            @Override
            public void onChanged(Point point) {
                if (point != null) {

                    mClNoMember.setVisibility(View.GONE);
                    mEtName.setText(point.getUserName());
                    mEtAvailablePoints.setText(String.valueOf(point.getPoints()));
                    mEtPointsRedeem.setEnabled(true);
                    mEtPointsRedeem.requestFocus();
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

                } else {

                    mClNoMember.setVisibility(View.VISIBLE);
                    mEtName.getText().clear();
                    mEtAvailablePoints.getText().clear();
                    mEtPointsRedeem.setEnabled(false);
                    mEtPointsRedeem.getText().clear();

                }
            }
        });

        mProductViewModel.getCart().observe(getViewLifecycleOwner(), new Observer<Cart>() {
            @Override
            public void onChanged(Cart cart) {
                mEtSubtotal.setText(getString(R.string.tvSubtotal, cart.getSubtotal()));
                mEtDiscount.setText(getString(R.string.tvDiscount, cart.getDiscount()));
                mEtTotal.setText(getString(R.string.tvTotal, cart.getTotal()));
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {

            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

            if (result != null)
                mCheckoutViewModel.assignPoint(result.getContents());
        }
    }
}
