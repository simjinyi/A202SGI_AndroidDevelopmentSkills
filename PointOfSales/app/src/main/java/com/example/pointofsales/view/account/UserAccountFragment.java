package com.example.pointofsales.view.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.pointofsales.R;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.UserType;
import com.example.pointofsales.model.state.UserAccountFormState;
import com.example.pointofsales.viewmodel.UserAccountViewModel;
import com.example.pointofsales.viewmodel.UserAccountViewModelFactory;
import com.example.pointofsales.viewmodel.UserViewModel;

public class UserAccountFragment extends AccountFormFragment {

    private UserAccountViewModel mUserAccountViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mUserAccountViewModel = ViewModelProviders.of(this, new UserAccountViewModelFactory(UserViewModel.getUserId())).get(UserAccountViewModel.class);

        mSwChangePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mUserAccountViewModel.setEnableChangePassword(isChecked);
            }
        });

        mUserAccountViewModel.getUnmatchedPassword().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(getActivity(), getString(R.string.unmatched_original_password), Toast.LENGTH_SHORT).show();
                    mUserAccountViewModel.clearUnmatchedPasswordFlag();
                }
            }
        });

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

        mUserAccountViewModel.getUserUpdated().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(getActivity(), getString(R.string.seller_details_updated), Toast.LENGTH_SHORT).show();
                    mUserAccountViewModel.clearUserUpdatedFlag();
                    getFragmentManager().popBackStack();
                }
            }
        });

        mEtName.addTextChangedListener(afterTextChangedListener);
        mEtEmail.addTextChangedListener(afterTextChangedListener);
        mEtPassword.addTextChangedListener(afterTextChangedListener);
        mEtAddress.addTextChangedListener(afterTextChangedListener);
        mEtPointsPerPrice.addTextChangedListener(afterTextChangedListener);
        mEtOriginalPassword.addTextChangedListener(afterTextChangedListener);
        mEtNewPassword.addTextChangedListener(afterTextChangedListener);

        mUserAccountViewModel.getUserData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                setData(user);
            }
        });

        mUserAccountViewModel.getAccountFormEnableState().observe(getViewLifecycleOwner(), this);
        setData(UserViewModel.getUser());
    }

    @Override
    public void submit() {
        mUserAccountViewModel.updateUser(getData());
    }

    @Override
    public Pair<User, String> getData() {

        User user = new User();

        user.setName(mEtName.getText().toString());
        user.setEmail(mEtEmail.getText().toString());
        user.setType(UserType.SELLER);
        user.setPassword(mEtNewPassword.getText().toString());

        return new Pair<>(user, mEtOriginalPassword.getText().toString());
    }
}
