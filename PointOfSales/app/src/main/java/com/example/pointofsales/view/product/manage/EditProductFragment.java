package com.example.pointofsales.view.product.manage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.view.OnSingleClickListener;
import com.example.pointofsales.view.product.ProductFragment;
import com.example.pointofsales.viewmodel.ProductViewModel;
import com.google.android.gms.tasks.OnSuccessListener;

/**
 * EditProductFragment inherits from ProductFormFragment
 * Handles the product edit action
 */
public class EditProductFragment extends ProductFormFragment {

    // ViewModel
    private ProductViewModel mProductViewModel;

    // Original product passed to the fragment
    private Product mOriProduct;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get the ViewModel
        mProductViewModel = ViewModelProviders.of(getActivity()).get(ProductViewModel.class);

        // Get the product object based on the index passed through the fragment argument
        mOriProduct = mProductViewModel.getProductList().getValue().get(getArguments().getInt(ProductFragment.PRODUCT_INDEX_FRAGMENT_ARG));

        // Set the product data into the form
        setData(mOriProduct);

        // Allow product deletion
        mBtnDelete.setVisibility(View.VISIBLE);
        mBtnDelete.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                // Prompt confirmation before deletion
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.delete_product))
                        .setMessage(getString(R.string.delete_product_confirmation))
                        .setIcon(R.drawable.ic_warning_24dp)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Calls the ViewModel to delete the product
                                mProductViewModel.deleteProduct(mOriProduct);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        // Observes the changes on the product removed state
        mProductViewModel.getProductRemoved().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {

                    // If the product was deleted successfully
                    // Prompt a success message and navigate back to home
                    Toast.makeText(getActivity(), getResources().getString(R.string.product_deleted_successfully), Toast.LENGTH_SHORT).show();
                    mProductViewModel.clearProductRemoved();
                    getFragmentManager().popBackStack();
                }
            }
        });
    }

    @Override
    public void submit() {
        mLoadingScreen.start();

        // Calls the ViewModel to update the product
        mProductViewModel.updateProduct(mOriProduct, getProductObject(), new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                mLoadingScreen.end();
                if (o == null) {

                    // If the object o is null, denotes no error and the product was updated successfully
                    // Goes back to the home fragment
                    Toast.makeText(getActivity(), getResources().getString(R.string.product_updated_successfully), Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStack();
                } else {

                    // Prompt the error where the product name was duplicated
                    Toast.makeText(getActivity(), getResources().getString(R.string.product_name_duplicate), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
