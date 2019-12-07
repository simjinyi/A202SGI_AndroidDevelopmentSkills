package com.example.pointofsales.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.R;
import com.example.pointofsales.model.state.ProductFormState;

/**
 * ProductFormViewModel validates the product form
 */
public class ProductFormViewModel extends ViewModel {

    // MutableLiveData to be observed by the View components
    private MutableLiveData<ProductFormState> mProductFormState;

    public ProductFormViewModel() {

        // Instantiate the MutableLiveData
        mProductFormState = new MutableLiveData<>();
        mProductFormState.setValue(new ProductFormState(false));
    }

    /**
     * Validate if the product form fields are valid
     * @param productName product name to be validated
     * @param productPrice product price to be validated
     * @param productInventory product inventory quantity to be validated
     * @param productPoints product points to be validated
     */
    public void productFormDataChanged(String productName, String productPrice, String productInventory, String productPoints) {

        float productPriceParsed = 0.0f;
        boolean isProductPriceValid = true;

        int productInventoryParsed = 0;
        boolean isProductInventoryValid = true;

        int productPointsParsed = 0;
        boolean isProductPointsValid = true;

        try {
            productPriceParsed = Float.parseFloat(productPrice);

            // Check range
            if (productPriceParsed <= 0 || productPriceParsed > 9999)
                isProductPriceValid = false;
        } catch (Exception e) {
            isProductPriceValid = false;
        }

        try {
            productInventoryParsed = Integer.parseInt(productInventory);

            // Check range
            if (productInventoryParsed < 0 || productInventoryParsed > 99999)
                isProductInventoryValid = false;
        } catch (Exception e) {
            isProductInventoryValid = false;
        }

        try {
            productPointsParsed = Integer.parseInt(productPoints);

            // Check range
            if (productPointsParsed < 0 || productPointsParsed > 99999)
                isProductPointsValid = false;
        } catch (Exception e) {
            isProductPointsValid = false;
        }

        if (productName.trim().isEmpty()) // Check empty product name
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

    // GETTER METHOD
    public LiveData<ProductFormState> getProductFormState() {
        return mProductFormState;
    }
    // END GETTER METHOD
}
