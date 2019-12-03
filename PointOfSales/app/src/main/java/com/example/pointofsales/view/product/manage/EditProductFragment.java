package com.example.pointofsales.view.product.manage;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.pointofsales.R;
import com.example.pointofsales.helper.ConfirmationDialogHelper;
import com.example.pointofsales.repository.ProductInterface;
import com.example.pointofsales.view.OnSingleClickListener;
import com.example.pointofsales.view.product.ProductFragment;
import com.example.pointofsales.viewmodel.ProductViewModel;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.repository.ProductRepository;
import com.example.pointofsales.viewmodel.ProductViewModelFactory;
import com.example.pointofsales.viewmodel.UserViewModel;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Map;

public class EditProductFragment extends ProductFormFragment {

    private ProductViewModel mProductViewModel;
    private Product mOriProduct;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mProductViewModel = ViewModelProviders.of(this, new ProductViewModelFactory(UserViewModel.getUserId())).get(ProductViewModel.class);
        mOriProduct = mProductViewModel.getProductList().getValue().get(getArguments().getInt(ProductFragment.PRODUCT_INDEX_FRAGMENT_ARG));

        setData(mOriProduct);

        mBtnDelete.setVisibility(View.VISIBLE);
        mBtnDelete.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                ConfirmationDialogHelper.getConfirmationDialog(getActivity(),
                        getResources().getString(R.string.delete_product_confirmation),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mProductViewModel.deleteProduct(mOriProduct);
                            }
                        }).show();
            }
        });

        mProductViewModel.getProductRemoved().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.product_deleted_successfully), Toast.LENGTH_SHORT).show();
                    mProductViewModel.clearProductRemoved();
                    getFragmentManager().popBackStack();
                }
            }
        });
    }

    @Override
    public void submit() {
        mLoadingScreenHelper.start();
        mProductViewModel.updateProduct(mOriProduct, getProductObject(), new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                mLoadingScreenHelper.end();
                if (o == null) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.product_updated_successfully), Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.product_name_duplicate), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
