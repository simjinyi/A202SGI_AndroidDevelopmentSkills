package com.example.pointofsales.model.state;

/**
 * Model class to store the profile form validation cases
 */
public class StoreAccountFormState extends UserAccountFormState {

    private Integer mAddressError;
    private Integer mPointsPerPriceError;

    public StoreAccountFormState(Integer nameError, Integer emailError, Integer passwordError, Integer originalPasswordErrorInteger, Integer newPasswordError, Integer addressError, Integer pointsPerPriceError) {
        super(nameError, emailError, passwordError, originalPasswordErrorInteger, newPasswordError);
        mAddressError = addressError;
        mPointsPerPriceError = pointsPerPriceError;
    }

    public StoreAccountFormState(boolean isDataValid) {
        super(isDataValid);
    }

    public Integer getAddressError() {
        return mAddressError;
    }

    public Integer getPointsPerPriceError() {
        return mPointsPerPriceError;
    }
}
