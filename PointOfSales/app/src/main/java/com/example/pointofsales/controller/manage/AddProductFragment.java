package com.example.pointofsales.controller.manage;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.example.pointofsales.R;
import com.example.pointofsales.controller.product.ProductViewModel;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.repository.ProductRepository;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Map;

public class AddProductFragment extends ProductFormFragment {

    private ProductViewModel mProductViewModel;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mProductViewModel = ViewModelProviders.of(getActivity()).get(ProductViewModel.class);
    }

    @Override
    public void submit() {

        Product product = getProductObject();
        Map<String, Object> map = ProductRepository.productToMap(product);
        mLoadingScreenHelper.start();

        if (!mProductViewModel.checkIfNameExists(product.getName())) {
            ProductRepository.getInstance().insertIntoDatabase(map, "0", new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.product_added_successfully), Toast.LENGTH_SHORT).show();
                    mLoadingScreenHelper.end();
                    getActivity().onBackPressed();
                }
            });
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.product_name_duplicate), Toast.LENGTH_SHORT).show();
            mLoadingScreenHelper.end();
        }
    }
}