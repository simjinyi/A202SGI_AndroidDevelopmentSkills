package com.example.pointofsales.view.account;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.pointofsales.model.state.ProductFormState;
import com.example.pointofsales.model.state.StoreAccountFormState;
import com.example.pointofsales.viewmodel.StoreAccountViewModel;
import com.example.pointofsales.viewmodel.StoreAccountViewModelFactory;
import com.example.pointofsales.viewmodel.UserViewModel;

public class StoreAccountFragment extends AccountFormFragment {

    private StoreAccountViewModel mStoreAccountViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mStoreAccountViewModel = ViewModelProviders.of(this, new StoreAccountViewModelFactory(UserViewModel.getUserId())).get(StoreAccountViewModel.class);

        mSwChangePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mStoreAccountViewModel.setEnableChangePassword(isChecked);
            }
        });

        mStoreAccountViewModel.getStoreAccountFormState().observe(getViewLifecycleOwner(), new Observer<StoreAccountFormState>() {
            @Override
            public void onChanged(StoreAccountFormState storeAccountFormState) {
                if (storeAccountFormState == null)
                    return;

                // mBtnSubmit.setEnabled(productFormState.isDataValid());

                if (storeAccountFormState.getNameError() != null)
                    mEtName.setError(getString(storeAccountFormState.getNameError()));

                if (storeAccountFormState.getEmailError() != null)
                    mEtEmail.setError(getString(storeAccountFormState.getEmailError()));

                if (storeAccountFormState.getPasswordError() != null)
                    mEtPassword.setError(getString(storeAccountFormState.getPasswordError()));

                if (storeAccountFormState.getAddressError() != null)
                    mEtAddress.setError(getString(storeAccountFormState.getAddressError()));

                if (storeAccountFormState.getPointsPerPriceError() != null)
                    mEtPointsPerPrice.setError(getString(storeAccountFormState.getPointsPerPriceError()));

                if (storeAccountFormState.getOriginalPasswordError() != null)
                    mEtOriginalPassword.setError(getString(storeAccountFormState.getOriginalPasswordError()));

                if (storeAccountFormState.getNewPasswordError() != null)
                    mEtNewPassword.setError(getString(storeAccountFormState.getNewPasswordError()));
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
                mStoreAccountViewModel.storeAccountFormChanged(mEtName.getText().toString(),
                        mEtEmail.getText().toString(),
                        mEtPassword.getText().toString(),
                        mEtOriginalPassword.getText().toString(),
                        mEtNewPassword.getText().toString(),
                        mEtAddress.getText().toString(),
                        mEtPointsPerPrice.getText().toString());
            }
        };

        mEtName.addTextChangedListener(afterTextChangedListener);
        mEtEmail.addTextChangedListener(afterTextChangedListener);
        mEtPassword.addTextChangedListener(afterTextChangedListener);
        mEtAddress.addTextChangedListener(afterTextChangedListener);
        mEtPointsPerPrice.addTextChangedListener(afterTextChangedListener);
        mEtOriginalPassword.addTextChangedListener(afterTextChangedListener);
        mEtNewPassword.addTextChangedListener(afterTextChangedListener);

        mStoreAccountViewModel.getAccountFormEnableState().observe(getViewLifecycleOwner(), this);
        setData(UserViewModel.getUser());
    }
}
