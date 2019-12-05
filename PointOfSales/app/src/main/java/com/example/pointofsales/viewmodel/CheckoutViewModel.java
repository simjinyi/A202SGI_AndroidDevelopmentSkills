package com.example.pointofsales.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pointofsales.model.Point;
import com.example.pointofsales.model.PointsRedeemedAndAwarded;
import com.example.pointofsales.model.Store;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.UserType;
import com.example.pointofsales.repository.PointRepository;
import com.example.pointofsales.repository.UserRepository;
import com.example.pointofsales.view.checkout.ScanListener;
import com.example.pointofsales.view.checkout.UpdatePointInterface;

public class CheckoutViewModel extends ViewModel implements UpdatePointInterface {

    private MutableLiveData<Integer> mPointsRedeemedError;
    private MutableLiveData<Boolean> mScanUserNotFound;
    private MutableLiveData<Point> mPoint;
    private MutableLiveData<Integer> mEditTextValue;
    private MutableLiveData<PointsRedeemedAndAwarded> mPointsRedeemedAndAwarded;

    private Store mStore;

    private PointRepository mPointRepository;
    private UserRepository mUserRepository;

    private ProductViewModel mProductViewModel;


    public CheckoutViewModel(ProductViewModel productViewModel) {

        mPointRepository = PointRepository.getInstance(UserViewModel.getUser(), this);
        mUserRepository = UserRepository.getInstance();

        mScanUserNotFound = new MutableLiveData<>();
        mScanUserNotFound.setValue(false);

        mStore = (Store) UserRepository.getInstance().getUser().getValue();

        mPoint = mPointRepository.getPoint();
        mPointsRedeemedAndAwarded = mPointRepository.getPointsRedeemedAndAwarded();

        mPointsRedeemedError = new MutableLiveData<>();
        mPointsRedeemedError.setValue(null);

        mEditTextValue = new MutableLiveData<>();
        mEditTextValue.setValue(0);

        mProductViewModel = productViewModel;
    }

    public void assignPoint(String userId) {
        mUserRepository.get(userId, UserType.CUSTOMER, new ScanListener() {
            @Override
            public void getUser(User user) {
                if (user == null) {
                    mScanUserNotFound.setValue(true);
                } else {
                    mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded());
                    mPoint.setValue(findPointByUser(user));
                }
            }
        });
    }
    
    public void removePoint() {
        mPointRepository.clearPoint();
    }

    public int updateDiscount(int pointsRedeemed) {

        if (pointsRedeemed == 0) {

            mProductViewModel.getCart().getValue().setDiscount(0.0f);
            mProductViewModel.notifyCartObservers();
            return -1;
        }

        int pointsPerPrice = ((Store) UserViewModel.getUser()).getPointsPerPrice();
        float subTotal = mProductViewModel.getCart().getValue().getSubtotal();
        int maxPointsAvailable = mPoint.getValue().getPoints();

        float discount = (float) pointsRedeemed / pointsPerPrice;

        if (discount > subTotal) {

            mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded(0, 0));
            mProductViewModel.getCart().getValue().setDiscount(0.0f);
            mProductViewModel.notifyCartObservers();
            return (int) subTotal * pointsPerPrice;

        } else if (maxPointsAvailable < pointsRedeemed) {

            mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded(0, 0));
            mProductViewModel.getCart().getValue().setDiscount(0.0f);
            mProductViewModel.notifyCartObservers();
            return mPoint.getValue().getPoints();

        } else {

            mPointsRedeemedAndAwarded.setValue(new PointsRedeemedAndAwarded(pointsRedeemed, 0));
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
        mPointRepository.clearPoint();
        mPointRepository.clearInstance();
    }

    public void clearScanUserNotFoundFlag() {
        mScanUserNotFound.setValue(false);
    }

    private Point findPointByUser(User user) {
        for (Point point : mPointRepository.getPoints().getValue())
            if (point.getUserId().equals(user.getId()))
                return point;

        Point point = new Point(user.getName(), user.getId(), mStore.getName(), mStore.getId(), 0);
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

    @Override
    public void onPointChanged(Point point) {
        mEditTextValue.setValue(point.getPoints());
    }

    @Override
    public void onPointDeleted() {
        clearPoint();
    }
}
