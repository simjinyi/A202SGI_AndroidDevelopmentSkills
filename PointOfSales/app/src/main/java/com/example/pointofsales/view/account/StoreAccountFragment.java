package com.example.pointofsales.view.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Store;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.UserType;
import com.example.pointofsales.model.state.StoreAccountFormState;
import com.example.pointofsales.model.state.UserUpdatedState;
import com.example.pointofsales.view.OnSingleClickListener;
import com.example.pointofsales.view.UserValidationActivity;
import com.example.pointofsales.view.login.LoginActivity;
import com.example.pointofsales.viewmodel.CheckoutViewModel;
import com.example.pointofsales.viewmodel.CheckoutViewModelFactory;
import com.example.pointofsales.viewmodel.ProductViewModel;
import com.example.pointofsales.viewmodel.StoreAccountViewModel;
import com.example.pointofsales.viewmodel.StoreAccountViewModelFactory;
import com.example.pointofsales.viewmodel.UserViewModel;

/**
 * StoreAccountFragment inherits from the AccountFormFragment
 * Handles the edit profile of the Seller
 */
public class StoreAccountFragment extends AccountFormFragment {

    // ViewModel objects
    private StoreAccountViewModel mStoreAccountViewModel;
    private ProductViewModel mProductViewModel;
    private CheckoutViewModel mCheckoutViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Obtain the ViewModels from the ViewModelProviders and provide the ViewModel persistence scope
        mProductViewModel = ViewModelProviders.of(getActivity()).get(ProductViewModel.class);
        mCheckoutViewModel = ViewModelProviders.of(getActivity(), new CheckoutViewModelFactory(mProductViewModel)).get(CheckoutViewModel.class);
        mStoreAccountViewModel = ViewModelProviders.of(this, new StoreAccountViewModelFactory(mCheckoutViewModel)).get(StoreAccountViewModel.class);

        // Enable change password fields when the switch is checked
        mSwChangePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mStoreAccountViewModel.setEnableChangePassword(isChecked);
            }
        });

        // Observes the unmatched password event from the ViewModel
        mStoreAccountViewModel.getUnmatchedPassword().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {

                // If the original password was unmatched
                if (aBoolean) {

                    // Prompt the error and clear the error flag in the ViewModel
                    Toast.makeText(getActivity(), getString(R.string.unmatched_original_password), Toast.LENGTH_SHORT).show();
                    mStoreAccountViewModel.clearUnmatchedPasswordFlag();
                    mLoadingScreen.end();
                }
            }
        });

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
                    Toast.makeText(getActivity(), getString(R.string.seller_details_updated), Toast.LENGTH_SHORT).show();
                    mStoreAccountViewModel.clearUserUpdatedFlag();
                    getFragmentManager().popBackStack();

                } else if (userUpdatedState.equals(UserUpdatedState.FAILED)) {

                    // Failed update as the email exists
                    Toast.makeText(getActivity(), getString(R.string.username_exists), Toast.LENGTH_SHORT).show();
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
                getFragmentManager().popBackStack();
            }
        });

        // Observes changes made to the user data
        mStoreAccountViewModel.getUserData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {

                // Update the form with the new data accordingly
                setData(user);
            }
        });

        // Set form fields enable status based on the AccountFormEnableState object returned
        mStoreAccountViewModel.getAccountFormEnableState().observe(getViewLifecycleOwner(), this);
        setData(UserViewModel.getUser());
    }

    /**
     * Start loading and update the store details
     */
    @Override
    public void submit() {
        mLoadingScreen.start();
        mStoreAccountViewModel.updateStore(getData());
    }

    /**
     * Get the details of the form entered by the user
     * @return a pair of the new Store object with the original password passed for validation
     */
    @Override
    public Pair<Store, String> getData() {

        Store store = new Store();

        store.setName(mEtName.getText().toString());
        store.setAddress(mEtAddress.getText().toString());
        store.setEmail(mEtEmail.getText().toString());
        store.setPointsPerPrice(Integer.parseInt(mEtPointsPerPrice.getText().toString()));
        store.setType(UserType.SELLER);
        store.setPassword(mEtNewPassword.getText().toString());

        return new Pair<>(store, mEtOriginalPassword.getText().toString());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.logout_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_bar_logout) {

            // Handle logout
            ((UserValidationActivity) getActivity()).invalidateLoginState();
            UserViewModel.logout();

            // Navigate to the LoginActivity without history to prevent backpress from coming back to this page
            Intent i = new Intent(getActivity(), LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            getActivity().finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
