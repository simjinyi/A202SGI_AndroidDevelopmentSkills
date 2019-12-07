package com.example.pointofsales.view.product.manage;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.example.pointofsales.R;
import com.example.pointofsales.viewmodel.ProductViewModel;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * AddProductFragment inherits from ProductFormFragment
 * Handles the product addition
 */
public class AddProductFragment extends ProductFormFragment {

    // ViewModel
    private ProductViewModel mProductViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProductViewModel = ViewModelProviders.of(getActivity()).get(ProductViewModel.class);
    }

    @Override
    public void submit() {
        mLoadingScreen.start();

        // Calls the ViewModel to insert the product
        mProductViewModel.insertProduct(getProductObject(), new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                mLoadingScreen.end();
                if (o == null) {

                    // If the object o is null, denotes no error and the product was added successfully
                    // Goes back to the home fragment
                    Toast.makeText(getActivity(), getResources().getString(R.string.product_added_successfully), Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStack();
                } else {

                    // Prompt the error where the product name was duplicated
                    Toast.makeText(getActivity(), getResources().getString(R.string.product_name_duplicate), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}