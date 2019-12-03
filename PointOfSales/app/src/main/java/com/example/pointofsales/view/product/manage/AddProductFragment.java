package com.example.pointofsales.view.product.manage;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.example.pointofsales.R;
import com.example.pointofsales.viewmodel.ProductViewModel;
import com.google.android.gms.tasks.OnSuccessListener;

public class AddProductFragment extends ProductFormFragment {

    private ProductViewModel mProductViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProductViewModel = ViewModelProviders.of(getActivity()).get(ProductViewModel.class);
    }

    @Override
    public void submit() {
        mLoadingScreenHelper.start();
        mProductViewModel.insertProduct(getProductObject(), new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                mLoadingScreenHelper.end();
                if (o == null) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.product_added_successfully), Toast.LENGTH_SHORT).show();
                    getFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.product_name_duplicate), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}