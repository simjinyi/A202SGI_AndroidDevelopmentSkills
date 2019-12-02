package com.example.pointofsales.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.R;
import com.example.pointofsales.model.state.ProductFormState;

public class ProductFormViewModel extends ViewModel {

    private MutableLiveData<ProductFormState> mProductFormState;

    public ProductFormViewModel() {
        mProductFormState = new MutableLiveData<>();
        mProductFormState.setValue(new ProductFormState(false));
    }

    public void productFormDataChanged(String productName, String productPrice, String productInventory, String productPoints) {

        float productPriceParsed = 0.0f;
        boolean isProductPriceValid = true;

        int productInventoryParsed = 0;
        boolean isProductInventoryValid = true;

        int productPointsParsed = 0;
        boolean isProductPointsValid = true;

        try {
            productPriceParsed = Float.parseFloat(productPrice);

            if (productPriceParsed <= 0)
                isProductPriceValid = false;
        } catch (Exception e) {
            isProductPriceValid = false;
        }

        try {
            productInventoryParsed = Integer.parseInt(productInventory);

            if (productInventoryParsed < 0)
                isProductInventoryValid = false;
        } catch (Exception e) {
            isProductInventoryValid = false;
        }

        try {
            productPointsParsed = Integer.parseInt(productPoints);

            if (productPointsParsed < 0)
                isProductPointsValid = false;
        } catch (Exception e) {
            isProductPointsValid = false;
        }

        if (productName.trim().isEmpty())
            mProductFormState.setValue(new ProductFormState(R.string.invalid_product_name, null, null, null));
        else if (!isProductPriceValid)
            mProductFormState.setValue(new ProductFormState(null, R.string.invalid_product_price, null, null));
        else if (!isProductInventoryValid)
            mProductFormState.setValue(new ProductFormState(null, null, R.string.invalid_product_inventory, null));
        else if (!isProductPointsValid)
            mProductFormState.setValue(new ProductFormState(null, null, null, R.string.invalid_product_points));
        else
            mProductFormState.setValue(new ProductFormState(true));
    }

    public LiveData<ProductFormState> getProductFormState() {
        return mProductFormState;
    }
}
