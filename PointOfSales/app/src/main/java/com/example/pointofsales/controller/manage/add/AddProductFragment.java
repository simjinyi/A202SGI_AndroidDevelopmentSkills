package com.example.pointofsales.controller.manage.add;

import android.widget.Toast;

import com.example.pointofsales.R;
import com.example.pointofsales.controller.manage.ProductDetailsFragment;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.repository.ProductRepository;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Map;

public class AddProductFragment extends ProductDetailsFragment {

    @Override
    public void submit() {

        Product product = getProductObject();
        Map<String, Object> map = ProductRepository.productToMap(product);
        mLoadingScreenHelper.start();

        ProductRepository.getInstance().insertIntoDatabase(map, "0", new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                if (o != null) {
                    if (o.equals(ProductRepository.DUPLICATED_NAME))
                        Toast.makeText(getActivity(), getResources().getString(R.string.product_name_duplicate), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getActivity(), getResources().getString(R.string.product_unknown_error), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.product_added_successfully), Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                }

                mLoadingScreenHelper.end();
            }
        });
    }
}