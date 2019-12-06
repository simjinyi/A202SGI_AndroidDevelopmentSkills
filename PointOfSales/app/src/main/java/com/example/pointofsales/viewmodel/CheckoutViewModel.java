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

public class CheckoutViewModel extends ViewModel implements UpdatePointInterface {

    private MutableLiveData<Boolean> mCheckoutLoading;
    private MutableLiveData<Boolean> mCheckoutDone;
    private MutableLiveData<Integer> mPointsRedeemedError;
    private MutableLiveData<Boolean> mScanUserNotFound;
    private MutableLiveData<Point> mPoint;
    private MutableLiveData<Integer> mEditTextValue;
    private MutableLiveData<PointsRedeemedAndAwarded> mPointsRedeemedAndAwarded;
    private MutableLiveData<Boolean> mMemberPointChangedState;

    private Store mStore;

    private PointRepository mPointRepository;
    private UserRepository mUserRepository;
    private TransactionRepository mTransactionRepository;

    private ProductViewModel mProductViewModel;


    public CheckoutViewModel(ProductViewModel productViewModel) {
        init(productViewModel);
    }

    private void init(ProductViewModel productViewModel) {

        mPointRepository = PointRepository.getInstance(UserViewModel.getUser(), this);
        mUserRepository = UserRepository.getInstance();
        mTransactionRepository = TransactionRepository.getInstance(UserViewModel.getUser(), null, null);

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

    public void updatePoint(Point point, boolean pointsPerPriceChanged) {
        if (mPointsRedeemedAndAwarded.getValue() == null)
            return;

        int pointsPerPrice = ((Store) UserViewModel.getUser()).getPointsPerPrice();
        float subTotal = mProductViewModel.getCart().getValue().getSubtotal();
        float discount = (float) mPointsRedeemedAndAwarded.getValue().getRedeemedPoint() / pointsPerPrice;

        if (discount > subTotal || point != null) {
            mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded((int) subTotal * pointsPerPrice, calculatePointAwarded()));
            mProductViewModel.getCart().getValue().setDiscount((float) mPointsRedeemedAndAwarded.getValue().getRedeemedPoint() / pointsPerPrice);
            mMemberPointChangedState.setValue(true);
            mProductViewModel.notifyCartObservers();
        } else if (pointsPerPriceChanged) {
            mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded(mPointsRedeemedAndAwarded.getValue().getRedeemedPoint(), calculatePointAwarded()));
            mProductViewModel.getCart().getValue().setDiscount((float) mPointsRedeemedAndAwarded.getValue().getRedeemedPoint() / pointsPerPrice);
            mMemberPointChangedState.setValue(true);
            mProductViewModel.notifyCartObservers();
        }

        mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded(mPointsRedeemedAndAwarded.getValue().getRedeemedPoint(), calculatePointAwarded()));
        notifyPointChanged();
    }

    public void assignPoint(final String userId) {
        mUserRepository.get(userId, UserType.CUSTOMER, new ScanListener() {
            @Override
            public void getUser(User user) {
                if (user == null) {
                    mScanUserNotFound.setValue(true);
                } else {
                    if (mPoint.getValue() != null)
                        clearPoint();

                    mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded(0, calculatePointAwarded()));
                    mPoint.setValue(findPointByUser(user));
                }
            }
        });
    }

    public int updateDiscount(int pointsRedeemed) {
        if (mPoint.getValue() == null)
            return -1;

        if (pointsRedeemed == 0) {

            mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded(0, calculatePointAwarded()));
            mProductViewModel.getCart().getValue().setDiscount(0.0f);
            mProductViewModel.notifyCartObservers();
            return -1;
        }

        int pointsPerPrice = ((Store) UserViewModel.getUser()).getPointsPerPrice();
        float subTotal = mProductViewModel.getCart().getValue().getSubtotal();
        int maxPointsAvailable = mPoint.getValue().getPoints();

        float discount = (float) pointsRedeemed / pointsPerPrice;

        if (maxPointsAvailable < pointsRedeemed) {

            mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded(0, calculatePointAwarded()));
            mEditTextValue.setValue(0);
            mProductViewModel.getCart().getValue().setDiscount(0.0f);
            mProductViewModel.notifyCartObservers();
            return mPoint.getValue().getPoints();

        } else if (discount > subTotal) {

            mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded(0, calculatePointAwarded()));
            mEditTextValue.setValue(0);
            mProductViewModel.getCart().getValue().setDiscount(0.0f);
            mProductViewModel.notifyCartObservers();
            return (int) subTotal * pointsPerPrice;

        } else {

            mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded(pointsRedeemed, calculatePointAwarded()));
            mProductViewModel.getCart().getValue().setDiscount(discount);
            mProductViewModel.notifyCartObservers();
            return -1;
        }
    }

    public void redeemPointChanged(String pointsRedeemed) {
        if (pointsRedeemed.isEmpty())
            pointsRedeemed = "0";

        int returned = -1;
        if ((returned = updateDiscount(Integer.parseInt(pointsRedeemed))) != -1) {
            mPointsRedeemedError.setValue(returned);
        } else {
            mPointsRedeemedError.setValue(null);
        }
    }

    public void updateEditText() {
        if (mPointsRedeemedAndAwarded.getValue() != null)
            mEditTextValue.setValue(mPointsRedeemedAndAwarded.getValue().getRedeemedPoint());
    }

    public void clearPoint() {
        mProductViewModel.getCart().getValue().setDiscount(0.0f);
        mProductViewModel.notifyCartObservers();
        mPointRepository.clearPoint();
        mPointRepository.clearInstance();
    }

    public void clearMemberPointChangedState() {
        mMemberPointChangedState.setValue(false);
    }

    public void clearScanUserNotFoundFlag() {
        mScanUserNotFound.setValue(false);
    }

    public void clearCheckoutDone() {
        mCheckoutDone.setValue(false);
    }

    public int calculatePointAwarded() {
        int total = 0;
        ArrayList<Product> cartList = mProductViewModel.getCartList().getValue();

        for (Product product : cartList)
            total += product.getPointPerItem() * product.getCartQuantity();

        return total;
    }

    public void checkout() {

        mCheckoutLoading.setValue(true);

        ArrayList<Product> cartList = mProductViewModel.getCartList().getValue();
        Transaction transaction = new Transaction();

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

        for (Product product : cartList) {

            TransactionItem transactionItem = new TransactionItem();

            transactionItem.setName(product.getName());
            transactionItem.setPrice(product.getPrice());
            transactionItem.setQuantity(product.getCartQuantity());

            transaction.getTransactionItems().add(transactionItem);

            product.setInventoryQuantity(product.getInventoryQuantity() - product.getCartQuantity());
            product.setTotalSales(product.getTotalSales() + product.getCartQuantity());

            mProductViewModel.updateProduct(product, product, new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    // ignore
                }
            });
        }

        if (mPoint.getValue() != null) {
            Point point = mPoint.getValue();
            point.setPoints(point.getPoints() + mPointsRedeemedAndAwarded.getValue().getPointAwarded() - mPointsRedeemedAndAwarded.getValue().getRedeemedPoint());
            mPointRepository.update(point, new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {

                }
            });

            mProductViewModel.resetCart();
        }

        mTransactionRepository.insert(transaction, new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

                mCheckoutLoading.setValue(false);
                mCheckoutDone.setValue(true);
                init(mProductViewModel);
                mProductViewModel.resetCart();
                clearPoint();
            }
        });
    }

    public void notifyPointChanged() {
        mPoint.setValue(mPoint.getValue());
    }

    private Point findPointByUser(User user) {
        for (Point point : mPointRepository.getPoints().getValue())
            if (point.getUserId().equals(user.getId()))
                return point;

        Point point = new Point(user.getName(), user.getId(), mStore.getName(), mStore.getId(), mStore.getAddress(), mStore.getPointsPerPrice(), 0);
        mPointRepository.insert(point, null);
        return point;
    }

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

    @Override
    public void onPointChanged(Point point) {
        updatePoint(point, false);
        mEditTextValue.setValue(point.getPoints());
    }

    @Override
    public void onPointDeleted() {
        clearPoint();
    }
}
