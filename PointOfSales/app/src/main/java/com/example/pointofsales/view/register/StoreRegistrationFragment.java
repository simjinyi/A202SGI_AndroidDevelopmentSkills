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
import com.example.pointofsales.model.Store;
import com.example.pointofsales.model.UserType;
import com.example.pointofsales.model.state.StoreAccountFormState;
import com.example.pointofsales.model.state.UserUpdatedState;
import com.example.pointofsales.view.OnSingleClickListener;
import com.example.pointofsales.view.account.AccountFormFragment;
import com.example.pointofsales.viewmodel.StoreAccountViewModel;

/**
 * StoreRegistrationFragment inherits from the AccountFormFragment
 * Handles the registration of the Seller
 */
public class StoreRegistrationFragment extends AccountFormFragment {

    // ViewModel
    private StoreAccountViewModel mStoreAccountViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get the ViewModel from the ViewModelProviders
        mStoreAccountViewModel = ViewModelProviders.of(this).get(StoreAccountViewModel.class);

        // Observes the change on the StoreAccountFormState from the ViewModel for form validation
        mStoreAccountViewModel.getStoreAccountFormState().observe(getViewLifecycleOwner(), new Observer<StoreAccountFormState>() {
            @Override
            public void onChanged(StoreAccountFormState storeAccountFormState) {
                if (storeAccountFormState == null)
                    return;

                 mBtnSubmit.setEnabled(storeAccountFormState.isDataValid());

                // Name error
                if (storeAccountFormState.getNameError() != null)
                    mEtName.setError(getString(storeAccountFormState.getNameError()));

                // Email error
                if (storeAccountFormState.getEmailError() != null)
                    mEtEmail.setError(getString(storeAccountFormState.getEmailError()));

                // Password error
                if (storeAccountFormState.getPasswordError() != null)
                    mEtPassword.setError(getString(storeAccountFormState.getPasswordError()));

                // Address error
                if (storeAccountFormState.getAddressError() != null)
                    mEtAddress.setError(getString(storeAccountFormState.getAddressError()));

                // Points per price error
                if (storeAccountFormState.getPointsPerPriceError() != null)
                    mEtPointsPerPrice.setError(getString(storeAccountFormState.getPointsPerPriceError()));

                // Original password error
                if (storeAccountFormState.getOriginalPasswordError() != null)
                    mEtOriginalPassword.setError(getString(storeAccountFormState.getOriginalPasswordError()));

                // New password error
                if (storeAccountFormState.getNewPasswordError() != null)
                    mEtNewPassword.setError(getString(storeAccountFormState.getNewPasswordError()));
            }
        });

        // Create a textwatcher that updates the ViewModel with the latest form details when the form was changed
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

                // Calls the ViewModel for validation
                mStoreAccountViewModel.storeAccountFormChanged(mEtName.getText().toString(),
                        mEtEmail.getText().toString(),
                        mEtPassword.getText().toString(),
                        mEtOriginalPassword.getText().toString(),
                        mEtNewPassword.getText().toString(),
                        mEtAddress.getText().toString(),
                        mEtPointsPerPrice.getText().toString());
            }
        };

        // Observes the changes made to the user
        mStoreAccountViewModel.getUserUpdated().observe(getViewLifecycleOwner(), new Observer<UserUpdatedState>() {
            @Override
            public void onChanged(UserUpdatedState userUpdatedState) {
                if (userUpdatedState.equals(UserUpdatedState.SUCCESS)) {

                    // Successful update
                    Toast.makeText(getActivity(), getString(R.string.seller_inserted), Toast.LENGTH_SHORT).show();
                    mStoreAccountViewModel.clearUserUpdatedFlag();
                    getActivity().onBackPressed();

                } else if (userUpdatedState.equals(UserUpdatedState.FAILED)) {

                    // Failed update as the email exists
                    Toast.makeText(getActivity(), getString(R.string.username_exists), Toast.LENGTH_SHORT).show();
                    mStoreAccountViewModel.clearUserUpdatedFlag();
                }

                mLoadingScreen.end();
            }
        });

        // Add the textwatcher for all the editTexts in the form
        mEtName.addTextChangedListener(afterTextChangedListener);
        mEtEmail.addTextChangedListener(afterTextChangedListener);
        mEtPassword.addTextChangedListener(afterTextChangedListener);
        mEtAddress.addTextChangedListener(afterTextChangedListener);
        mEtPointsPerPrice.addTextChangedListener(afterTextChangedListener);
        mEtOriginalPassword.addTextChangedListener(afterTextChangedListener);
        mEtNewPassword.addTextChangedListener(afterTextChangedListener);

        // Go back to the previous fragment
        mBtnCancel.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                getActivity().onBackPressed();
            }
        });

        // Set form fields enable status based on the AccountFormEnableState object returned
        mStoreAccountViewModel.getAccountFormEnableState().observe(getViewLifecycleOwner(), this);
    }

    /**
     * Start loading and insert the store details
     */
    @Override
    public void submit() {
        mLoadingScreen.start();
        mStoreAccountViewModel.insertStore(getData());
    }

    /**
     * Get the details of the form entered by the user
     * @return a new Store object from the form data
     */
    @Override
    public Store getData() {

        Store store = new Store();

        store.setName(mEtName.getText().toString());
        store.setAddress(mEtAddress.getText().toString());
        store.setEmail(mEtEmail.getText().toString());
        store.setPointsPerPrice(Integer.parseInt(mEtPointsPerPrice.getText().toString()));
        store.setType(UserType.SELLER);
        store.setPassword(mEtPassword.getText().toString());

        return store;
    }
}
