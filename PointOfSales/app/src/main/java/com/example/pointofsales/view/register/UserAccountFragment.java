package com.example.pointofsales.view.register;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.pointofsales.R;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.UserType;
import com.example.pointofsales.model.state.UserAccountFormState;
import com.example.pointofsales.model.state.UserUpdatedState;
import com.example.pointofsales.view.OnSingleClickListener;
import com.example.pointofsales.view.account.AccountFormFragment;
import com.example.pointofsales.viewmodel.UserAccountViewModel;

public class UserAccountFragment extends AccountFormFragment {

    private UserAccountViewModel mUserAccountViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mUserAccountViewModel = ViewModelProviders.of(this).get(UserAccountViewModel.class);

        mUserAccountViewModel.getUserAccountFormState().observe(getViewLifecycleOwner(), new Observer<UserAccountFormState>() {
            @Override
            public void onChanged(UserAccountFormState userAccountFormState) {
                if (userAccountFormState == null)
                    return;

                mBtnSubmit.setEnabled(userAccountFormState.isDataValid());

                if (userAccountFormState.getNameError() != null)
                    mEtName.setError(getString(userAccountFormState.getNameError()));

                if (userAccountFormState.getEmailError() != null)
                    mEtEmail.setError(getString(userAccountFormState.getEmailError()));

                if (userAccountFormState.getPasswordError() != null)
                    mEtPassword.setError(getString(userAccountFormState.getPasswordError()));

                if (userAccountFormState.getOriginalPasswordError() != null)
                    mEtOriginalPassword.setError(getString(userAccountFormState.getOriginalPasswordError()));

                if (userAccountFormState.getNewPasswordError() != null)
                    mEtNewPassword.setError(getString(userAccountFormState.getNewPasswordError()));
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
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
                mUserAccountViewModel.userAccountFormChanged(mEtName.getText().toString(),
                        mEtEmail.getText().toString(),
                        mEtPassword.getText().toString(),
                        mEtOriginalPassword.getText().toString(),
                        mEtNewPassword.getText().toString());
            }
        };

        mUserAccountViewModel.getUserUpdated().observe(getViewLifecycleOwner(), new Observer<UserUpdatedState>() {
            @Override
            public void onChanged(UserUpdatedState userUpdatedState) {
                if (userUpdatedState.equals(UserUpdatedState.SUCCESS)) {
                    Toast.makeText(getActivity(), getString(R.string.user_inserted), Toast.LENGTH_SHORT).show();
                    mUserAccountViewModel.clearUserUpdatedFlag();
                    getActivity().onBackPressed();
                } else if (userUpdatedState.equals(UserUpdatedState.FAILED)) {
                    Toast.makeText(getActivity(), getString(R.string.username_exists), Toast.LENGTH_SHORT).show();
                    mUserAccountViewModel.clearUserUpdatedFlag();
                }

                mLoadingScreen.end();
            }
        });

        mEtName.addTextChangedListener(afterTextChangedListener);
        mEtEmail.addTextChangedListener(afterTextChangedListener);
        mEtPassword.addTextChangedListener(afterTextChangedListener);
        mEtAddress.addTextChangedListener(afterTextChangedListener);
        mEtPointsPerPrice.addTextChangedListener(afterTextChangedListener);
        mEtOriginalPassword.addTextChangedListener(afterTextChangedListener);
        mEtNewPassword.addTextChangedListener(afterTextChangedListener);

        mBtnCancel.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getActivity().onBackPressed();
            }
        });

        mUserAccountViewModel.getAccountFormEnableState().observe(getViewLifecycleOwner(), this);
    }

    @Override
    public void submit() {
        mLoadingScreen.start();
        mUserAccountViewModel.insertUser(getData());
    }

    @Override
    public User getData() {

        User user = new User();

        user.setName(mEtName.getText().toString());
        user.setEmail(mEtEmail.getText().toString());
        user.setType(UserType.CUSTOMER);
        user.setPassword(mEtPassword.getText().toString());

        return user;
    }
}
