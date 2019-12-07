package com.example.pointofsales.view.qr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.example.pointofsales.R;
import com.example.pointofsales.model.QR;
import com.example.pointofsales.model.User;
import com.example.pointofsales.utility.LoadingScreen;
import com.example.pointofsales.view.OnSingleClickListener;
import com.example.pointofsales.viewmodel.UserViewModel;

/**
 * QR Code Fragment to show the QR code on the hom page
 */
public class QRCodeFragment extends Fragment {

    // View components
    private TextView mTvUsername;
    private ImageButton mIbAccount;
    private ImageView mIvQRCode;
    private ProgressBar mPbLoading;
    private LoadingScreen mLoadingScreen;

    // ViewModel
    private UserViewModel mUserViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Assign the reference to the view components
        mTvUsername = getView().findViewById(R.id.tvUsername);
        mIbAccount = getView().findViewById(R.id.ibAccount);
        mIvQRCode = getView().findViewById(R.id.ivQRCode);
        mPbLoading = getView().findViewById(R.id.pbLoading);

        mLoadingScreen = new LoadingScreen(getActivity(), mPbLoading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get the ViewModel from the ViewModelProviders
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mLoadingScreen.start();

        // Observe the changes on the user data
        mUserViewModel.getUserLiveData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {
                mLoadingScreen.end();

                // Update the view component and the QR Code
                mTvUsername.setText(user.getName());
                mIvQRCode.setImageBitmap(QR.generateQRCode(user.getId()));
            }
        });

        mIbAccount.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                // Navigate to the edit account fragment
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_qr_to_navigation_manage_user);
            }
        });
    }

}
