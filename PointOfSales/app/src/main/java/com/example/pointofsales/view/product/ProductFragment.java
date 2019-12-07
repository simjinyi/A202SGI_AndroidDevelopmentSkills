package com.example.pointofsales.view.product;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
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

import com.example.pointofsales.R;
import com.example.pointofsales.model.Cart;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.model.User;
import com.example.pointofsales.model.state.CartOpenableState;
import com.example.pointofsales.model.state.CartRemovalState;
import com.example.pointofsales.model.state.ProductInventoryQuantityChangeState;
import com.example.pointofsales.model.state.ProductLoadState;
import com.example.pointofsales.utility.LoadingScreen;
import com.example.pointofsales.view.OnSingleClickListener;
import com.example.pointofsales.viewmodel.CheckoutViewModel;
import com.example.pointofsales.viewmodel.CheckoutViewModelFactory;
import com.example.pointofsales.viewmodel.ProductViewModel;
import com.example.pointofsales.viewmodel.UserViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

/**
 * ProductFragment handles the product page
 * Implements the EditButtonClick interface to allow callback on the position of the edit product button clicked
 */
public class ProductFragment extends Fragment implements EditButtonClick {

    public static final String PRODUCT_INDEX_FRAGMENT_ARG = "com.example.pointofsales.view.product.PRODUCT_INDEX_FRAGMENT_ARG";

    // ViewModels
    private ProductViewModel mProductViewModel;
    private UserViewModel mUserViewModel;
    private CheckoutViewModel mCheckoutViewModel;
    private ProductAdapter mProductAdapter;

    // View components
    private TextView mTvUsername;
    private ImageButton mIbAccount;
    private RecyclerView mRvProductList;
    private FloatingActionButton mFabAddProduct;
    private TextView mTvTotalPrice;
    private ImageButton mIbCancelCart;
    private TextView mTvCartQuantity;
    private ImageButton mIbCheckout;
    private ProgressBar mPbLoading;
    private CardView mCvNoProduct;

    private LoadingScreen mLoadingScreen;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Assign the view components
        mTvUsername = getView().findViewById(R.id.tvUsername);
        mIbAccount = getView().findViewById(R.id.ibAccount);
        mRvProductList = getView().findViewById(R.id.rvProductList);
        mFabAddProduct = getView().findViewById(R.id.fabAddProduct);
        mTvTotalPrice = getView().findViewById(R.id.tvTotalPrice);
        mIbCancelCart = getView().findViewById(R.id.ibCancelCart);
        mTvCartQuantity = getView().findViewById(R.id.tvCartQuantity);
        mIbCheckout = getView().findViewById(R.id.ibCheckout);
        mPbLoading = getView().findViewById(R.id.pbLoading);
        mCvNoProduct = getView().findViewById(R.id.cvNoProduct);

        mLoadingScreen = new LoadingScreen(getActivity(), mPbLoading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get the ViewModels from the ViewModelProviders specifying the persistence scope of the data
        mProductViewModel = ViewModelProviders.of(getActivity()).get(ProductViewModel.class);
        mCheckoutViewModel = ViewModelProviders.of(getActivity(), new CheckoutViewModelFactory(mProductViewModel)).get(CheckoutViewModel.class);
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        mProductAdapter = new ProductAdapter(getActivity(), this, mProductViewModel, mCheckoutViewModel);
        mProductAdapter.setHasStableIds(true);
        mLoadingScreen.start();

        // Observes the product loading state
        mProductViewModel.getProductLoadState().observe(getViewLifecycleOwner(), new Observer<ProductLoadState>() {
            @Override
            public void onChanged(ProductLoadState productLoadState) {

                // Update the view accordingly based on the existence of the product, loading or done loaded
                switch (productLoadState) {
                    case LOADING:
                        mLoadingScreen.start();
                        mCvNoProduct.setVisibility(View.GONE);
                        break;

                    case LOADED:
                        mLoadingScreen.end();
                        mCvNoProduct.setVisibility(View.GONE);
                        break;

                    default:
                        mLoadingScreen.end();
                        mCvNoProduct.setVisibility(View.VISIBLE);
                }
            }
        });

        // Observes the changes on the product list
        mProductViewModel.getProductList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Product>>() {
            @Override
            public void onChanged(ArrayList<Product> products) {

                // Update the view and update the membership points if necessary
                mProductAdapter.notifyDataSetChanged();
                mCheckoutViewModel.updatePoint(null, false);
            }
        });

        // Populate the Product RecyclerView by setting the adapter
        mRvProductList.addItemDecoration(new ProductDecorator(getResources().getDimension(R.dimen.product_list_item_margin)));
        mRvProductList.setLayoutManager(new ProductGridLayoutManager(getActivity(), 2));
        mRvProductList.setAdapter(mProductAdapter);

        // Add product clicked
        mFabAddProduct.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                // Navigate to the add product fragment
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_product_to_navigation_add_product);
            }
        });

        // Cancel cart clicked
        mIbCancelCart.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                // Prompt confirmation before clearing the cart
                new AlertDialog.Builder(getActivity())
                        .setTitle(getString(R.string.clear_cart))
                        .setMessage(getString(R.string.clear_cart_description))
                        .setIcon(R.drawable.ic_warning_24dp)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // Reset the cart by calling the ViewModel and prompt a success message
                                mProductViewModel.resetCart();
                                Toast.makeText(getActivity(), getResources().getString(R.string.cart_cleared_successfully), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        // Observe the changes on the cart items
        mProductViewModel.getCart().observe(getViewLifecycleOwner(), new Observer<Cart>() {
            @Override
            public void onChanged(Cart cart) {

                // Update the price and cart quantity
                mTvTotalPrice.setText(getResources().getString(R.string.tvTotalPrice, cart.getSubtotal()));
                mTvCartQuantity.setVisibility((cart.getCartQuantity() > 0) ? View.VISIBLE : View.GONE);
                mTvCartQuantity.setText(String.valueOf(cart.getCartQuantity()));
            }
        });

        // Observes the cart openable state
        mProductViewModel.getCartOpenableState().observe(getViewLifecycleOwner(), new Observer<CartOpenableState>() {
            @Override
            public void onChanged(CartOpenableState cartOpenableState) {

                // Update the button enable state in navigating to the checkout page based on the availability of the cart
                if (cartOpenableState.equals(CartOpenableState.ENABLED)) {
                    mIbCheckout.setEnabled(true);
                    mIbCancelCart.setEnabled(true);
                } else {
                    mIbCheckout.setEnabled(false);
                    mIbCancelCart.setEnabled(false);
                    mCheckoutViewModel.clearPoint();
                }
            }
        });

        // Checkout button clicked
        mIbCheckout.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                // Navigate to the checkout page
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_product_to_navigation_checkout);
            }
        });

        // Observes and check if the cart quantity changed due to a change in the product inventory quantity
        mProductViewModel.getProductInventoryQuantityChangeState().observe(getViewLifecycleOwner(), new Observer<ProductInventoryQuantityChangeState>() {
            @Override
            public void onChanged(ProductInventoryQuantityChangeState productInventoryQuantityChangeState) {
                if (productInventoryQuantityChangeState.getProductNames().size() > 0) {

                    // Prompt a notification on the cart changed
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.product_quantity_changed))
                            .setMessage(getString(R.string.product_quantity_changed_description, productInventoryQuantityChangeState.toString()))
                            .setIcon(R.drawable.ic_info_24dp)
                            .setCancelable(false)
                            .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    mProductViewModel.clearProductInventoryQuantityChangeFlag();
                }
            }
        });

        // Observes and check if the cart product removed due to product removal
        mProductViewModel.getCartRemovalState().observe(getViewLifecycleOwner(), new Observer<CartRemovalState>() {
            @Override
            public void onChanged(CartRemovalState cartRemovalState) {
                if (cartRemovalState.getProductNames().size() > 0) {

                    // Prompt a notification on the cart removal
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.product_removed_from_cart))
                            .setMessage(getString(R.string.product_removed_from_cart_description, cartRemovalState.toString()))
                            .setIcon(R.drawable.ic_info_24dp)
                            .setCancelable(false)
                            .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    mProductViewModel.clearCartRemovalFlag();
                }
            }
        });

        // Observe if the product was missing
        mProductViewModel.getProductMissing().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {

                    // Prompt an error showing that the product is missing and no operations can be performed
                    Toast.makeText(getActivity(), getString(R.string.product_missing), Toast.LENGTH_SHORT).show();
                    mProductViewModel.clearProductMissingFlag();
                }
            }
        });

        // Edit profile clicked
        mIbAccount.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {

                // Navigate to the edit profile page
                Navigation.findNavController(getView()).navigate(R.id.action_navigation_product_to_navigation_manage_store);
            }
        });

        // Add the ItemTouchHelper to allow rearrangement of items in the RecyclerView
        ItemTouchHelper.Callback callback = new ProductItemTouchHelper(mProductAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRvProductList);

        // Observes the changes made to the user details
        mUserViewModel.getUserLiveData().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {

                // Update the view with the latest data
                mTvUsername.setText(user.getName());
            }
        });

        // Observe the changes on the membership
        mCheckoutViewModel.getMemberChangedState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {

                    // If the membership points was changed due to a change in the cart or the membership point itself
                    // Prompt a notification
                    new AlertDialog.Builder(getActivity())
                            .setTitle(getString(R.string.member_point_changed))
                            .setMessage(getString(R.string.member_point_changed_description))
                            .setIcon(R.drawable.ic_info_24dp)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();
                    mCheckoutViewModel.clearMemberPointChangedState();
                }
            }
        });
    }

    @Override
    public void onEditButtonClick(Product product) {

        // Navigate to the edit product page with the product index in the dataset passed as the fragment argument
        Bundle bundle = new Bundle();
        bundle.putInt(PRODUCT_INDEX_FRAGMENT_ARG, mProductViewModel.getProductIndexFromProduct(product));
        Navigation.findNavController(getView()).navigate(R.id.action_navigation_product_to_navigation_edit_product, bundle);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.seach_sort_menu, menu);

        // Handle the actionbar search feature
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.product_query_hint));

        // Apply the filter
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mProductAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mProductAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.app_bar_sort) {

            // Sort the membership and prompt the message on the way the items were sorted
            String sortText = getString(mProductViewModel.sort());
            item.setTitle(sortText);
            Toast.makeText(getActivity(), getString(R.string.sorting_by, sortText), Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}