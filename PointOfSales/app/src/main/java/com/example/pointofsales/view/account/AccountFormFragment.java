package com.example.pointofsales.view.account;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.example.pointofsales.database.UserDatabase;
import com.example.pointofsales.helper.LoadingScreenHelper;
import com.example.pointofsales.model.Store;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.state.AccountFormEnableState;
import com.example.pointofsales.repository.CartRepository;
import com.example.pointofsales.repository.ProductRepository;
import com.example.pointofsales.repository.UserRepository;
import com.example.pointofsales.view.MainActivity;
import com.example.pointofsales.view.OnSingleClickListener;
import com.example.pointofsales.view.login.LoginActivity;
import com.example.pointofsales.viewmodel.UserViewModel;

public abstract class AccountFormFragment extends Fragment implements Observer<AccountFormEnableState> {

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
    private Button mBtnSubmit;
    private Button mBtnCancel;
    protected ProgressBar mPbLoading;

    private LoadingScreenHelper mLoadingScreenHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        mLoadingScreenHelper = new LoadingScreenHelper(getActivity(), mPbLoading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mBtnCancel.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        mBtnSubmit.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                submit();
            }
        });
    }

    @Override
    public void onChanged(AccountFormEnableState accountFormEnableState) {
        mEtEmail.setEnabled(accountFormEnableState.isEmailEnabled());

        mTvPasswordLabel.setVisibility(accountFormEnableState.isPasswordVisible() ? View.VISIBLE : View.GONE);
        mEtPassword.setVisibility(accountFormEnableState.isPasswordVisible() ? View.VISIBLE : View.GONE);
        if (!accountFormEnableState.isPasswordVisible())
            mEtPassword.setText(null);

        mTvAddressLabel.setVisibility(accountFormEnableState.isAddressVisible() ? View.VISIBLE : View.GONE);
        mEtAddress.setVisibility(accountFormEnableState.isAddressVisible() ? View.VISIBLE : View.GONE);
        if (!accountFormEnableState.isAddressVisible())
            mEtAddress.setText(null);

        mTvPointsPerPriceLabel.setVisibility(accountFormEnableState.isPointsPerPriceVisible() ? View.VISIBLE : View.GONE);
        mEtPointsPerPrice.setVisibility(accountFormEnableState.isPointsPerPriceVisible() ? View.VISIBLE : View.GONE);
        if (!accountFormEnableState.isPointsPerPriceVisible())
            mEtPointsPerPrice.setText(null);

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

    public void setData(User user) {

        mEtName.setText(user.getName());
        mEtEmail.setText(user.getEmail());

        if (user instanceof Store) {
            mEtAddress.setText(((Store) user).getAddress());
            mEtPointsPerPrice.setText(String.valueOf(((Store) user).getPointsPerPrice()));
        }
    }

    public abstract void submit();
    public abstract Object getData();

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.logout_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_bar_logout) {
            UserViewModel.logout();
            Intent i = new Intent(getActivity(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            getActivity().finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
