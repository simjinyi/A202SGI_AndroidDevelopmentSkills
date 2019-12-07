package com.example.pointofsales.view.account;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Store;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.state.AccountFormEnableState;
import com.example.pointofsales.utility.LoadingScreen;
import com.example.pointofsales.view.OnSingleClickListener;

/**
 * Abstract class for the account (profile) form fragment, since the same view can be shared between both Customer and Seller
 * Implements the Observer<AccountFormEnableState> to listen to any changes on the form fields enable status, and update the view components respectively
 * This class will be used in registration for both Seller and Customer and the profile update for both Seller and Customer
 */
public abstract class AccountFormFragment extends Fragment implements Observer<AccountFormEnableState> {

    // View components
    protected EditText mEtName;
    protected EditText mEtEmail;
    private TextView mTvPasswordLabel;
    protected EditText mEtPassword;
    private TextView mTvAddressLabel;
    protected EditText mEtAddress;
    private TextView mTvPointsPerPriceLabel;
    protected EditText mEtPointsPerPrice;
    protected EditText mEtOriginalPassword;
    protected EditText mEtNewPassword;
    protected Switch mSwChangePassword;
    private CardView mCvStorePasswordHolder;
    protected Button mBtnSubmit;
    protected Button mBtnCancel;
    protected ProgressBar mPbLoading;

    protected LoadingScreen mLoadingScreen;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the reference to the view components
        mEtName = getView().findViewById(R.id.etName);
        mEtEmail = getView().findViewById(R.id.etEmail);
        mTvPasswordLabel = getView().findViewById(R.id.tvPasswordLabel);
        mEtPassword = getView().findViewById(R.id.etPassword);
        mTvAddressLabel = getView().findViewById(R.id.tvAddressLabel);
        mEtAddress = getView().findViewById(R.id.etAddress);
        mTvPointsPerPriceLabel = getView().findViewById(R.id.tvPointsPerPriceLabel);
        mEtPointsPerPrice = getView().findViewById(R.id.etPointsPerPrice);
        mEtOriginalPassword = getView().findViewById(R.id.etOriginalPassword);
        mEtNewPassword = getView().findViewById(R.id.etNewPassword);
        mSwChangePassword = getView().findViewById(R.id.swChangePassword);
        mCvStorePasswordHolder = getView().findViewById(R.id.cvStorePasswordHolder);
        mBtnCancel = getView().findViewById(R.id.btnCancel);
        mBtnSubmit = getView().findViewById(R.id.btnSubmit);
        mPbLoading = getView().findViewById(R.id.pbLoading);

        mLoadingScreen = new LoadingScreen(getActivity(), mPbLoading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBtnSubmit.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                // Calls the abstract method overloaded by the child classes to handle the form submission
                // As the form should be handled differently for the customer and the seller
                submit();
            }
        });
    }

    @Override
    public void onChanged(AccountFormEnableState accountFormEnableState) {

        // Check and set the enable status of the name field based on the value from the accountFormEnableState object
        mEtName.setEnabled(accountFormEnableState.isNameEnabled());
        mEtEmail.setEnabled(accountFormEnableState.isEmailEnabled());

        // Check and set the enable and visibility status of the password field based on the value from the accountFormEnableState object
        mTvPasswordLabel.setVisibility(accountFormEnableState.isPasswordVisible() ? View.VISIBLE : View.GONE);
        mEtPassword.setVisibility(accountFormEnableState.isPasswordVisible() ? View.VISIBLE : View.GONE);
        if (!accountFormEnableState.isPasswordVisible())
            mEtPassword.setText(null);

        // Check and set the enable and visibility status of the address field based on the value from the accountFormEnableState object
        mTvAddressLabel.setVisibility(accountFormEnableState.isAddressVisible() ? View.VISIBLE : View.GONE);
        mEtAddress.setVisibility(accountFormEnableState.isAddressVisible() ? View.VISIBLE : View.GONE);
        if (!accountFormEnableState.isAddressVisible())
            mEtAddress.setText(null);

        // Check and set the enable and visibility status of the points per price field based on the value from the accountFormEnableState object
        mTvPointsPerPriceLabel.setVisibility(accountFormEnableState.isPointsPerPriceVisible() ? View.VISIBLE : View.GONE);
        mEtPointsPerPrice.setVisibility(accountFormEnableState.isPointsPerPriceVisible() ? View.VISIBLE : View.GONE);
        if (!accountFormEnableState.isPointsPerPriceVisible())
            mEtPointsPerPrice.setText(null);

        // Check and set the enable status of the change password field based on the value from the accountFormEnableState object
        mCvStorePasswordHolder.setVisibility(accountFormEnableState.isChangePasswordVisible() ? View.VISIBLE : View.GONE);
        mEtOriginalPassword.setEnabled(accountFormEnableState.isChangePasswordEnabled());
        mEtNewPassword.setEnabled(accountFormEnableState.isChangePasswordEnabled());
        if (!accountFormEnableState.isChangePasswordEnabled()) {
            mEtOriginalPassword.setText(null);
            mEtOriginalPassword.setError(null);
            mEtNewPassword.setText(null);
            mEtNewPassword.setError(null);
        }
    }

    /**
     * Set the user details into the form
     * @param user user details to be set into the form
     */
    public void setData(User user) {

        mEtName.setText(user.getName());
        mEtEmail.setText(user.getEmail());

        if (user instanceof Store) {
            mEtAddress.setText(((Store) user).getAddress());
            mEtPointsPerPrice.setText(String.valueOf(((Store) user).getPointsPerPrice()));
        }
    }

    // ABSTRACT METHODS
    public abstract void submit();
    public abstract Object getData();
    // END ABSTRACT METHODS
}
