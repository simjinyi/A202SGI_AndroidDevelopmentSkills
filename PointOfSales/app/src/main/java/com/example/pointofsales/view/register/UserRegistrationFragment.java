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

/**
 * UserRegistrationFragment inherits from the AccountFormFragment
 * Handles the edit profile of the Customer
 */
public class UserRegistrationFragment extends AccountFormFragment {

    // ViewModel object
    private UserAccountViewModel mUserAccountViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Obtain the ViewModel from the ViewModelProviders and provide the ViewModel persistence scope
        mUserAccountViewModel = ViewModelProviders.of(this).get(UserAccountViewModel.class);

        // Observes the change on the UserAccountFormState from the ViewModel for form validation
        mUserAccountViewModel.getUserAccountFormState().observe(getViewLifecycleOwner(), new Observer<UserAccountFormState>() {
            @Override
            public void onChanged(UserAccountFormState userAccountFormState) {
                if (userAccountFormState == null)
                    return;

                mBtnSubmit.setEnabled(userAccountFormState.isDataValid());

                // Name error
                if (userAccountFormState.getNameError() != null)
                    mEtName.setError(getString(userAccountFormState.getNameError()));

                // Email error
                if (userAccountFormState.getEmailError() != null)
                    mEtEmail.setError(getString(userAccountFormState.getEmailError()));

                // Password error
                if (userAccountFormState.getPasswordError() != null)
                    mEtPassword.setError(getString(userAccountFormState.getPasswordError()));

                // Original password error
                if (userAccountFormState.getOriginalPasswordError() != null)
                    mEtOriginalPassword.setError(getString(userAccountFormState.getOriginalPasswordError()));

                // New password error
                if (userAccountFormState.getNewPasswordError() != null)
                    mEtNewPassword.setError(getString(userAccountFormState.getNewPasswordError()));
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
                mUserAccountViewModel.userAccountFormChanged(mEtName.getText().toString(),
                        mEtEmail.getText().toString(),
                        mEtPassword.getText().toString(),
                        mEtOriginalPassword.getText().toString(),
                        mEtNewPassword.getText().toString());
            }
        };

        // Observes the changes made to the user
        mUserAccountViewModel.getUserUpdated().observe(getViewLifecycleOwner(), new Observer<UserUpdatedState>() {
            @Override
            public void onChanged(UserUpdatedState userUpdatedState) {
                if (userUpdatedState.equals(UserUpdatedState.SUCCESS)) {

                    // Successful update
                    Toast.makeText(getActivity(), getString(R.string.user_inserted), Toast.LENGTH_SHORT).show();
                    mUserAccountViewModel.clearUserUpdatedFlag();
                    getActivity().onBackPressed();

                } else if (userUpdatedState.equals(UserUpdatedState.FAILED)) {

                    // Failed update as the email exists
                    Toast.makeText(getActivity(), getString(R.string.username_exists), Toast.LENGTH_SHORT).show();
                    mUserAccountViewModel.clearUserUpdatedFlag();
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
        mUserAccountViewModel.getAccountFormEnableState().observe(getViewLifecycleOwner(), this);
    }

    /**
     * Start loading and insert the user details
     */
    @Override
    public void submit() {
        mLoadingScreen.start();
        mUserAccountViewModel.insertUser(getData());
    }

    /**
     * Get the details of the form entered by the user
     * @return a new User object from the form data
     */
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
