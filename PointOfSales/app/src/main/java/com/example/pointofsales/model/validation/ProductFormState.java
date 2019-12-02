package com.example.pointofsales.model.validation;

public class ProductFormState {

    private Integer mNameError;
    private Integer mPriceError;
    private Integer mInventoryError;
    private Integer mPointsError;

    private boolean mIsDataValid;

    public ProductFormState(Integer nameError, Integer priceError, Integer inventoryError, Integer pointsError) {
        mNameError = nameError;
        mPriceError = priceError;
        mInventoryError = inventoryError;
        mPointsError = pointsError;
        mIsDataValid = false;
    }

    public ProductFormState(boolean isDataValid) {
        mNameError = mPriceError = mInventoryError = mPointsError = null;
        mIsDataValid = isDataValid;
    }

    public Integer getNameError() {
        return mNameError;
    }

    public Integer getPriceError() {
        return mPriceError;
    }

    public Integer getInventoryError() {
        return mInventoryError;
    }

    public Integer getPointsError() {
        return mPointsError;
    }

    public boolean isDataValid() {
        return mIsDataValid;
    }
}
