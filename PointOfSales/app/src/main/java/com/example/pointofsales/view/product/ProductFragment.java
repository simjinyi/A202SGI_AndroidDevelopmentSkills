package com.example.pointofsales.view.product;

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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.pointofsales.R;
import com.example.pointofsales.helper.ConfirmationDialogHelper;
import com.example.pointofsales.helper.LoadingScreenHelper;
import com.example.pointofsales.model.Cart;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.model.validation.CartOpenableState;
import com.example.pointofsales.model.validation.ProductInventoryQuantityChangeState;
import com.example.pointofsales.model.validation.ProductLoadState;
import com.example.pointofsales.view.OnSingleClickListener;
import com.example.pointofsales.viewmodel.ProductViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ProductFragment extends Fragment implements EditButtonClick {

    public static final String PRODUCT_INDEX_FRAGMENT_ARG = "com.example.pointofsales.view.product.PRODUCT_INDEX_FRAGMENT_ARG";

    private ProductViewModel mProductViewModel;
    private ProductAdapter mProductAdapter;

    private RecyclerView mRvProductList;
    private FloatingActionButton mFabAddProduct;
    private TextView mTvTotalPrice;
    private ImageButton mIbCancelCart;
    private TextView mTvCartQuantity;
    private ImageButton mIbCheckout;
    private ProgressBar mPbLoading;
    private CardView mCvNoProduct;

    private LoadingScreenHelper mLoadingScreenHelper;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRvProductList = getView().findViewById(R.id.rvProductList);
        mFabAddProduct = getView().findViewById(R.id.fabAddProduct);
        mTvTotalPrice = getView().findViewById(R.id.tvTotalPrice);
        mIbCancelCart = getView().findViewById(R.id.ibCancelCart);
        mTvCartQuantity = getView().findViewById(R.id.tvCartQuantity);
        mIbCheckout = getView().findViewById(R.id.ibCheckout);
        mPbLoading = getView().findViewById(R.id.pbLoading);
        mCvNoProduct = getView().findViewById(R.id.cvNoProduct);

        mLoadingScreenHelper = new LoadingScreenHelper(getActivity(), mPbLoading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mProductViewModel = ViewModelProviders.of(getActivity()).get(ProductViewModel.class);
        mProductAdapter = new ProductAdapter(getActivity(), this, mProductViewModel);
        mProductAdapter.setHasStableIds(true);
        mLoadingScreenHelper.start();

        mProductViewModel.getProductLoadState().observe(getViewLifecycleOwner(), new Observer<ProductLoadState>() {
            @Override
            public void onChanged(ProductLoadState productLoadState) {
                switch (productLoadState) {
                    case LOADING:
                        mLoadingScreenHelper.start();
                        mCvNoProduct.setVisibility(View.GONE);
                        break;

                    case LOADED:
                        mLoadingScreenHelper.end();
                        mCvNoProduct.setVisibility(View.GONE);
                        break;

                    default:
                        mLoadingScreenHelper.end();
                        mCvNoProduct.setVisibility(View.VISIBLE);
                }
            }
        });

        mProductViewModel.getProductList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Product>>() {
            @Override
            public void onChanged(ArrayList<Product> products) {
                mProductAdapter.notifyDataSetChanged();
            }
        });

        mRvProductList.addItemDecoration(new ProductDecorator(getResources().getDimension(R.dimen.product_list_item_margin)));
        mRvProductList.setLayoutManager(new ProductGridLayoutManager(getActivity(), 2));
        mRvProductList.setAdapter(mProductAdapter);

        mFabAddProduct.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_product_to_navigation_add_product);
            }
        });

        mIbCancelCart.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                ConfirmationDialogHelper.getConfirmationDialog(getActivity(),
                        getResources().getString(R.string.clear_cart_confirmation),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mProductViewModel.resetCart();
                                Toast.makeText(getActivity(), getResources().getString(R.string.cart_cleared_successfully), Toast.LENGTH_SHORT).show();
                            }
                        }).show();
            }
        });

        mProductViewModel.getCart().observe(getViewLifecycleOwner(), new Observer<Cart>() {
            @Override
            public void onChanged(Cart cart) {
                mTvTotalPrice.setText(getResources().getString(R.string.tvTotalPrice, cart.getSubtotal()));
                mTvCartQuantity.setVisibility((cart.getCartQuantity() > 0) ? View.VISIBLE : View.GONE);
                mTvCartQuantity.setText(String.valueOf(cart.getCartQuantity()));
            }
        });

        mProductViewModel.getCartOpenableState().observe(getViewLifecycleOwner(), new Observer<CartOpenableState>() {
            @Override
            public void onChanged(CartOpenableState cartOpenableState) {
                if (cartOpenableState.equals(CartOpenableState.ENABLED))
                    mIbCheckout.setEnabled(true);
                else
                    mIbCheckout.setEnabled(false);
            }
        });

        mIbCheckout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_product_to_navigation_checkout);
            }
        });

        mProductViewModel.getProductInventoryQuantityChangeState().observe(getViewLifecycleOwner(), new Observer<ProductInventoryQuantityChangeState>() {
            @Override
            public void onChanged(ProductInventoryQuantityChangeState productInventoryQuantityChangeState) {
                if (productInventoryQuantityChangeState.getProductNames().size() > 0)
                    Toast.makeText(getActivity(), productInventoryQuantityChangeState.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        ItemTouchHelper.Callback callback = new ProductItemTouchHelper(mProductAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRvProductList);
    }

    @Override
    public void onEditButtonClick(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt(PRODUCT_INDEX_FRAGMENT_ARG, position);
        Navigation.findNavController(getView()).navigate(R.id.action_navigation_product_to_navigation_edit_product, bundle);
    }
}