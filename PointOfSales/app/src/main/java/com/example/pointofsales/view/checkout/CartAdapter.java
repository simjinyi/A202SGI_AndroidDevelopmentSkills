package com.example.pointofsales.view.checkout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.view.OnSingleClickListener;
import com.example.pointofsales.viewmodel.CheckoutViewModel;
import com.example.pointofsales.viewmodel.ProductViewModel;

import java.util.ArrayList;

/**
 * CartAdapter extends from the RecyclerView.Adapter
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ProductViewModel mProductViewModel;
    private CheckoutViewModel mCheckoutViewModel;
    private ArrayList<Product> mCartList;

    /**
     * CartHolder of the cart item
     */
    public class CartHolder extends RecyclerView.ViewHolder {

        // View components
        private TextView mTvProductName;
        private TextView mTvProductPrice;
        private Button mBtnMinusProductQuantity;
        private TextView mTvProductQuantity;
        private Button mBtnAddProductQuantity;
        private TextView mTvProductPriceExtension;

        public CartHolder(@NonNull View itemView) {
            super(itemView);

            // Assign reference to the view components
            mTvProductName = itemView.findViewById(R.id.tvProductName);
            mTvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            mBtnMinusProductQuantity = itemView.findViewById(R.id.btnMinusProductQuantity);
            mTvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            mBtnAddProductQuantity = itemView.findViewById(R.id.btnAddProductQuantity);
            mTvProductPriceExtension = itemView.findViewById(R.id.tvProductPriceExtension);
        }

        /**
         * Bind the cart details to the view
         * @param product product object containing details to be updated
         * @param position adapter position
         */
        public void bindCart(Product product, final int position) {

            mTvProductName.setText(product.getName());
            mTvProductPrice.setText(String.format("%.2f", product.getPrice()));
            mTvProductQuantity.setText(String.valueOf(product.getCartQuantity()));
            mTvProductPriceExtension.setText(String.format("%.2f", product.getCartExtension()));

            // OnClickListener for the add product quantity operation
            mBtnAddProductQuantity.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {

                    // Calls the ViewModels for data and view update
                    mProductViewModel.addCartQuantity(mCartList.get(position));
                    mCheckoutViewModel.updatePoint(null);
                }
            });

            // OnClickListener for the minus product quantity operation
            mBtnMinusProductQuantity.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {

                    // Calls the ViewModels for data and view update
                    mProductViewModel.minusCartQuantity(mCartList.get(position));
                    mCheckoutViewModel.updatePoint(null);
                }
            });
        }
    }

    public CartAdapter(Context context, ProductViewModel productViewModel, CheckoutViewModel checkoutViewModel) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mProductViewModel = productViewModel;
        mCheckoutViewModel = checkoutViewModel;
        mCartList = mProductViewModel.getCartList().getValue();
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartHolder(mLayoutInflater.inflate(R.layout.list_item_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {
        holder.bindCart(mCartList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mCartList.size();
    }
}