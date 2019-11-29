package com.example.pointofsales.controller.product;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavArgument;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.pointofsales.R;
import com.example.pointofsales.helper.ConfirmationDialogHelper;
import com.example.pointofsales.helper.LoadingScreenHelper;
import com.example.pointofsales.model.ProductList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProductFragment extends Fragment implements EditButtonClick {

    private ProductViewModel mProductViewModel;
    private ProductAdapter mProductAdapter;

    private SwipeRefreshLayout mSrlProduct;
    private RecyclerView mRvProductList;
    private FloatingActionButton mFabAddProduct;
    private TextView mTvTotalPrice;
    private ImageButton mIbCancelCart;
    private TextView mTvCartQuantity;
    private ImageButton mIbCheckout;
    private ProgressBar mPbLoading;

    private LoadingScreenHelper mLoadingScreenHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSrlProduct = getView().findViewById(R.id.srlProduct);
        mRvProductList = getView().findViewById(R.id.rvProductList);
        mFabAddProduct = getView().findViewById(R.id.fabAddProduct);
        mTvTotalPrice = getView().findViewById(R.id.tvTotalPrice);
        mIbCancelCart = getView().findViewById(R.id.ibCancelCart);
        mTvCartQuantity = getView().findViewById(R.id.tvCartQuantity);
        mIbCheckout = getView().findViewById(R.id.ibCheckout);
        mPbLoading = getView().findViewById(R.id.pbLoading);

        mLoadingScreenHelper = new LoadingScreenHelper(getActivity(), mPbLoading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mProductViewModel = ViewModelProviders.of(getActivity()).get(ProductViewModel.class);
        mProductAdapter = new ProductAdapter(getActivity(), this, mProductViewModel);
        mProductAdapter.setHasStableIds(true);
        mLoadingScreenHelper.start();

        mProductViewModel.getProducts().observe(this, new Observer<ProductList>() {
            @Override
            public void onChanged(ProductList productList) {

                mProductAdapter.notifyDataSetChanged();
                mSrlProduct.setRefreshing(false);

                if (productList.getProductListSize() > 0)
                    mLoadingScreenHelper.end();
            }
        });

        mSrlProduct.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mProductViewModel.refresh();
            }
        });

        mRvProductList.addItemDecoration(new ProductDecorator(getResources().getDimension(R.dimen.product_list_item_margin)));
        mRvProductList.setLayoutManager(new ProductGridLayoutManager(getActivity(), 2));
        mRvProductList.setAdapter(mProductAdapter);

        mFabAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_product_to_navigation_add_product);
            }
        });

        mProductViewModel.getTotalPrice().observe(this, new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                mTvTotalPrice.setText(getResources().getString(R.string.tvTotalPrice, aFloat));
            }
        });

        mIbCancelCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmationDialogHelper.getConfirmationDialog(getActivity(),
                        getResources().getString(R.string.clear_cart_confirmation),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mProductViewModel.resetProductCartQuantity();
                                Toast.makeText(getActivity(), getResources().getString(R.string.cart_cleared_successfully), Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });

        mProductViewModel.getCartQuantity().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                mTvCartQuantity.setVisibility((integer > 0) ? View.VISIBLE : View.GONE);
                mTvCartQuantity.setText(String.valueOf(integer));
            }
        });

        mIbCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_product_to_navigation_checkout);
            }
        });
    }

    @Override
    public void onEditButtonClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("product_index", position);
        Navigation.findNavController(getView()).navigate(R.id.action_navigation_product_to_navigation_edit_product, bundle);
    }
}