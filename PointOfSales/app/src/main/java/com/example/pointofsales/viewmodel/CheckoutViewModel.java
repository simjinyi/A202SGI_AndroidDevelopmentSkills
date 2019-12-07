package com.example.pointofsales.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.model.Point;
import com.example.pointofsales.model.PointsRedeemedAndAwarded;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.model.Store;
import com.example.pointofsales.model.Transaction;
import com.example.pointofsales.model.TransactionItem;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.UserType;
import com.example.pointofsales.repository.PointRepository;
import com.example.pointofsales.repository.TransactionRepository;
import com.example.pointofsales.repository.UserRepository;
import com.example.pointofsales.view.checkout.ScanListener;
import com.example.pointofsales.view.checkout.UpdatePointInterface;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Date;

/**
 * CheckoutViewModel class
 * Implements UpdatePointInterface to callback on point (membership) changed or deleted
 */
public class CheckoutViewModel extends ViewModel implements UpdatePointInterface {

    // MutableLiveData to be observed by the view
    private MutableLiveData<Boolean> mCheckoutLoading;
    private MutableLiveData<Boolean> mCheckoutDone;
    private MutableLiveData<Integer> mPointsRedeemedError;
    private MutableLiveData<Boolean> mScanUserNotFound;
    private MutableLiveData<Point> mPoint;
    private MutableLiveData<Integer> mEditTextValue;
    private MutableLiveData<PointsRedeemedAndAwarded> mPointsRedeemedAndAwarded;
    private MutableLiveData<Boolean> mMemberPointChangedState;

    private Store mStore;

    // Repositories
    private PointRepository mPointRepository;
    private UserRepository mUserRepository;
    private TransactionRepository mTransactionRepository;

    private ProductViewModel mProductViewModel;

    public CheckoutViewModel(ProductViewModel productViewModel) {
        // Initialize the CheckoutViewModel
        init(productViewModel);
    }

    /**
     * Intialize the CheckoutViewModel
     * @param productViewModel productViewModel to be used in the initialization
     */
    private void init(ProductViewModel productViewModel) {

        // Get the repositories instance
        mPointRepository = PointRepository.getInstance(UserViewModel.getUser(), this);
        mUserRepository = UserRepository.getInstance();
        mTransactionRepository = TransactionRepository.getInstance(UserViewModel.getUser(), null, null);

        // Instantiates all the MutableLiveData
        mScanUserNotFound = new MutableLiveData<>();
        mScanUserNotFound.setValue(false);

        mStore = (Store) UserRepository.getInstance().getUser().getValue();

        mPoint = mPointRepository.getPoint();
        mPointsRedeemedAndAwarded = mPointRepository.getPointsRedeemedAndAwarded();

        mPointsRedeemedError = new MutableLiveData<>();
        mPointsRedeemedError.setValue(null);

        mEditTextValue = new MutableLiveData<>();
        mEditTextValue.setValue(0);

        mMemberPointChangedState = new MutableLiveData<>();
        mMemberPointChangedState.setValue(false);

        mCheckoutLoading = new MutableLiveData<>();
        mCheckoutLoading.setValue(false);

        mCheckoutDone = new MutableLiveData<>();
        mCheckoutDone.setValue(false);

        mProductViewModel = productViewModel;
    }

    /**
     * Update the point (membership) through the repository into the database
     * @param point point object to be updated
     * @param pointsPerPriceChanged if the pointsPerPrice of the store was changed
     */
    public void updatePoint(Point point, boolean pointsPerPriceChanged) {

        // If no member was added, shouldn't update
        if (mPointsRedeemedAndAwarded.getValue() == null)
            return;

        // Calculate the needed values
        int pointsPerPrice = ((Store) UserViewModel.getUser()).getPointsPerPrice();
        float subTotal = mProductViewModel.getCart().getValue().getSubtotal();
        float discount = (float) mPointsRedeemedAndAwarded.getValue().getRedeemedPoint() / pointsPerPrice;

        // If discount is greater than subtotal or the point (membership) is not equal to null
        if (discount > subTotal || point != null) {

            // Update the points redeemed and awarded, discount, changeState to true and notify the observers
            mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded((int) subTotal * pointsPerPrice, calculatePointAwarded()));
            mProductViewModel.getCart().getValue().setDiscount((float) mPointsRedeemedAndAwarded.getValue().getRedeemedPoint() / pointsPerPrice);
            mMemberPointChangedState.setValue(true);
            mProductViewModel.notifyCartObservers();

        } else if (pointsPerPriceChanged) {

            // Update the points redeemed and awarded, discount, changeState to true and notify the observers
            mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded(mPointsRedeemedAndAwarded.getValue().getRedeemedPoint(), calculatePointAwarded()));
            mProductViewModel.getCart().getValue().setDiscount((float) mPointsRedeemedAndAwarded.getValue().getRedeemedPoint() / pointsPerPrice);
            mMemberPointChangedState.setValue(true);
            mProductViewModel.notifyCartObservers();
        }

        mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded(mPointsRedeemedAndAwarded.getValue().getRedeemedPoint(), calculatePointAwarded()));
        notifyPointChanged();
    }

    /**
     * Assign the point (membership) after the QR code was scanned
     * @param userId scanned from the QE code
     */
    public void assignPoint(final String userId) {
        mUserRepository.get(userId, UserType.CUSTOMER, new ScanListener() {
            @Override
            public void getUser(User user) {
                if (user == null) {

                    // If the user doesn't exists, set scanUserNotFound flag to true
                    mScanUserNotFound.setValue(true);
                } else {

                    // If there was an existing member added, clear the member
                    if (mPoint.getValue() != null)
                        clearPoint();

                    // Assign the new member added
                    mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded(0, calculatePointAwarded()));
                    mPoint.setValue(findPointByUser(user));
                }
            }
        });
    }

    /**
     * Update the discount based on the pointsRedeemed
     * @param pointsRedeemed points redeemed
     * @return maximum point applicable (-1 if the point redeemed is valid)
     */
    public int updateDiscount(int pointsRedeemed) {
        // If the membership was not added, consider the point redeemed as valid since no validation is required
        if (mPoint.getValue() == null)
            return -1;

        // If the points redeemed is 0, no validation is required
        if (pointsRedeemed == 0) {

            // Update the cart details
            mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded(0, calculatePointAwarded()));
            mProductViewModel.getCart().getValue().setDiscount(0.0f);
            mProductViewModel.notifyCartObservers();
            return -1;
        }

        // Calculate the needed values
        int pointsPerPrice = ((Store) UserViewModel.getUser()).getPointsPerPrice();
        float subTotal = mProductViewModel.getCart().getValue().getSubtotal();
        int maxPointsAvailable = mPoint.getValue().getPoints();

        float discount = (float) pointsRedeemed / pointsPerPrice;

        // Error since the max point available by the user is less than the points attempted to be redeemed
        if (maxPointsAvailable < pointsRedeemed) {

            mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded(0, calculatePointAwarded()));
            mEditTextValue.setValue(0);
            mProductViewModel.getCart().getValue().setDiscount(0.0f);
            mProductViewModel.notifyCartObservers();
            return mPoint.getValue().getPoints();

        } else if (discount > subTotal) {

            // Error since the discount is greater than the subtotal causing the total price to be negative
            mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded(0, calculatePointAwarded()));
            mEditTextValue.setValue(0);
            mProductViewModel.getCart().getValue().setDiscount(0.0f);
            mProductViewModel.notifyCartObservers();
            return (int) subTotal * pointsPerPrice;

        } else {

            // The discount is valid
            mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded(pointsRedeemed, calculatePointAwarded()));
            mProductViewModel.getCart().getValue().setDiscount(discount);
            mProductViewModel.notifyCartObservers();
            return -1;
        }
    }

    /**
     * Validate redeem point entered
     * @param pointsRedeemed points to redeem
     */
    public void redeemPointChanged(String pointsRedeemed) {
        if (pointsRedeemed.isEmpty())
            pointsRedeemed = "0";

        int returned = -1;

        // Check if the redeem point is valid and set the error flag accordingly
        if ((returned = updateDiscount(Integer.parseInt(pointsRedeemed))) != -1) {
            mPointsRedeemedError.setValue(returned);
        } else {
            mPointsRedeemedError.setValue(null);
        }
    }

    /**
     * Update the editText with the points redeemed value
     */
    public void updateEditText() {
        if (mPointsRedeemedAndAwarded.getValue() != null)
            mEditTextValue.setValue(mPointsRedeemedAndAwarded.getValue().getRedeemedPoint());
    }

    /**
     * Clear the membership added to the transaction
     */
    public void clearPoint() {
        mProductViewModel.getCart().getValue().setDiscount(0.0f);
        mProductViewModel.notifyCartObservers();
        mPointRepository.clearPoint();
        mPointRepository.clearInstance();
    }

    // Flags clearer
    public void clearMemberPointChangedState() {
        mMemberPointChangedState.setValue(false);
    }

    public void clearScanUserNotFoundFlag() {
        mScanUserNotFound.setValue(false);
    }

    public void clearCheckoutDone() {
        mCheckoutDone.setValue(false);
    }
    // END Flags clearer

    /**
     * Calculate the points to be awarded for the transaction
     * @return
     */
    public int calculatePointAwarded() {
        int total = 0;
        ArrayList<Product> cartList = mProductViewModel.getCartList().getValue();

        for (Product product : cartList)
            total += product.getPointPerItem() * product.getCartQuantity();

        return total;
    }

    /**
     * Checkout and add the transaction
     */
    public void checkout() {

        mCheckoutLoading.setValue(true);

        // Don't observe product inventory quantity state change until the operation is done to prevent unnecessary notification being made
        mProductViewModel.setObserveProductInventoryQuantityChangeState(false);

        ArrayList<Product> cartList = mProductViewModel.getCartList().getValue();
        Transaction transaction = new Transaction();

        // Start to form the transaction object
        if (mPoint.getValue() == null) {
            transaction.setUserName(null);
            transaction.setUserId(null);

            transaction.setPointsRedeemed(null);
            transaction.setPointsAwarded(null);
        } else {
            transaction.setUserName(mPoint.getValue().getUserName());
            transaction.setUserId(mPoint.getValue().getUserId());

            transaction.setPointsRedeemed(mPointsRedeemedAndAwarded.getValue().getRedeemedPoint());
            transaction.setPointsAwarded(mPointsRedeemedAndAwarded.getValue().getPointAwarded());
        }

        transaction.setStoreName(mStore.getName());
        transaction.setStoreId(mStore.getId());

        transaction.setTimestamp(new Date().getTime());
        transaction.setSubtotal(mProductViewModel.getCart().getValue().getSubtotal());

        transaction.setSubtotal(mProductViewModel.getCart().getValue().getSubtotal());
        transaction.setDiscount(mProductViewModel.getCart().getValue().getDiscount());

        transaction.setTransactionItems(new ArrayList<TransactionItem>());
        // End forming the transaction object

        // Start to form the transaction item objects
        for (Product product : cartList) {

            TransactionItem transactionItem = new TransactionItem();

            transactionItem.setName(product.getName());
            transactionItem.setPrice(product.getPrice());
            transactionItem.setQuantity(product.getCartQuantity());

            transaction.getTransactionItems().add(transactionItem);

            product.setInventoryQuantity(product.getInventoryQuantity() - product.getCartQuantity());
            product.setTotalSales(product.getTotalSales() + product.getCartQuantity());

            // Update the product quantity after deducting the purchased quantity
            mProductViewModel.updateProduct(product, product, new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    // ignore
                }
            });
        }
        // End forming the transaction item objects

        // Set and update the point (membership) in the database through the repository for the transaction
        if (mPoint.getValue() != null) {
            Point point = mPoint.getValue();
            point.setPoints(point.getPoints() + mPointsRedeemedAndAwarded.getValue().getPointAwarded() - mPointsRedeemedAndAwarded.getValue().getRedeemedPoint());
            mPointRepository.update(point, new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    // ignore
                }
            });
        }

        // Insert the transaction into the database through the repository
        mTransactionRepository.insert(transaction, new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

                // Set done
                mCheckoutLoading.setValue(false);
                mCheckoutDone.setValue(true);
                init(mProductViewModel);

                // Reset the cart
                mProductViewModel.resetCart();
                clearPoint();

                // Observe the product inventory quantity state change again
                mProductViewModel.setObserveProductInventoryQuantityChangeState(true);
            }
        });
    }

    /**
     * Update the observers on the point (membership) updated
     */
    public void notifyPointChanged() {
        mPoint.setValue(mPoint.getValue());
    }

    // Find the point object by the user object
    private Point findPointByUser(User user) {

        // Find the point through the userId
        for (Point point : mPointRepository.getPoints().getValue())
            if (point.getUserId().equals(user.getId()))
                return point;

        // If the point (membership) is not found, add the user as a member and insert into the database
        Point point = new Point(user.getName(), user.getId(), mStore.getName(), mStore.getId(), mStore.getAddress(), mStore.getPointsPerPrice(), 0);
        mPointRepository.insert(point, null);
        return point;
    }

    // GETTER METHODS
    public LiveData<Point> getPoint() {
        return mPoint;
    }
    public LiveData<PointsRedeemedAndAwarded> getPointsRedeemedAndAwarded() {
        return mPointsRedeemedAndAwarded;
    }
    public LiveData<Boolean> getScanUserNotFound() {
        return mScanUserNotFound;
    }
    public LiveData<Integer> getPointsRedeemedError() {
        return mPointsRedeemedError;
    }
    public LiveData<Integer> getEditTextValue() {
        return mEditTextValue;
    }
    public LiveData<Boolean> getMemberChangedState() {
        return mMemberPointChangedState;
    }
    public LiveData<Boolean> getCheckoutLoading() {
        return mCheckoutLoading;
    }
    public LiveData<Boolean> getCheckoutDone() {
        return mCheckoutDone;
    }
    // END GETTER METHODS

    /**
     * Update the point when the point is changed and set the editTextValue
     * @param point point object updated
     */
    @Override
    public void onPointChanged(Point point) {
        updatePoint(point, false);
        mEditTextValue.setValue(point.getPoints());
    }

    /**
     * Clear the membership
     */
    @Override
    public void onPointDeleted() {
        clearPoint();
    }
}
