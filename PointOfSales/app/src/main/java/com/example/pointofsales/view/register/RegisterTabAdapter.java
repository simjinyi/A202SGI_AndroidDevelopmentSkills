package com.example.pointofsales.view.register;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.pointofsales.R;

/**
 * RegisterTabAdapter handles and populates the tabs
 */
public class RegisterTabAdapter extends FragmentPagerAdapter {

    private Integer pageTitles[] = { R.string.tab_customer, R.string.tab_seller };
    private Context mContext;

    public RegisterTabAdapter(@NonNull FragmentManager fm, int behavior, Context context) {
        super(fm, behavior);
        mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        // Instantiate the appropriate registration fragment (seller or customer) based on the position passed
        if (position > 0)
            return new StoreRegistrationFragment();
        return new UserRegistrationFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        // Set the tab title
        return mContext.getString(pageTitles[position]);
    }
}
