package com.example.pointofsales.view.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.example.pointofsales.view.login.LoginActivity;
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

        mStoreAccountViewModel.getUnmatchedPassword().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(getActivity(), getString(R.string.unmatched_original_password), Toast.LENGTH_SHORT).show();
                    mStoreAccountViewModel.clearUnmatchedPasswordFlag();
                }
            }
        });

        mStoreAccountViewModel.getStoreAccountFormState().observe(getViewLifecycleOwner(), new Observer<StoreAccountFormState>() {
            @Override
            public void onChanged(StoreAccountFormState storeAccountFormState) {
                if (storeAccountFormState == null)
                    return;

                 mBtnSubmit.setEnabled(storeAccountFormState.isDataValid());

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

        mStoreAccountViewModel.getUserUpdated().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(getActivity(), getString(R.string.seller_details_updated), Toast.LENGTH_SHORT).show();
                    mStoreAccountViewModel.clearUserUpdatedFlag();
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

        mStoreAccountViewModel.getUserData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                setData(user);
            }
        });

        mStoreAccountViewModel.getAccountFormEnableState().observe(getViewLifecycleOwner(), this);
        setData(UserViewModel.getUser());
    }

    @Override
    public void submit() {
        mStoreAccountViewModel.updateStore(getData());
    }

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
