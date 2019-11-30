package com.example.pointofsales.view.product.manage;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.example.pointofsales.R;
import com.example.pointofsales.view.product.ProductFragment;
import com.example.pointofsales.viewmodel.ProductViewModel;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.repository.ProductRepository;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Map;

public class EditProductFragment extends ProductFormFragment {

    private ProductViewModel mProductViewModel;
    private Product mOriProduct;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mProductViewModel = ViewModelProviders.of(getActivity()).get(ProductViewModel.class);
        mOriProduct = mProductViewModel.getProductList().getValue().get(getArguments().getInt(ProductFragment.PRODUCT_INDEX_FRAGMENT_ARG));

        setData(mOriProduct);
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
                    getActivity().onBackPressed();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.product_name_duplicate), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
