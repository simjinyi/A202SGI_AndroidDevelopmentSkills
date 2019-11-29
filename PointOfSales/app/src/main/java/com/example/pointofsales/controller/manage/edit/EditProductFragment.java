package com.example.pointofsales.controller.manage.edit;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.example.pointofsales.controller.ProductViewModel;
import com.example.pointofsales.controller.manage.ProductDetailsFragment;

public class EditProductFragment extends ProductDetailsFragment {

    private String mProductId;
    private ProductViewModel mProductViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mProductViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);

        mProductId = getArguments().getString("product_id", null);
        setData(mProductViewModel.getProducts().getValue().getProductById(mProductId));
    }

    @Override
    public void submit() {

    }
}
