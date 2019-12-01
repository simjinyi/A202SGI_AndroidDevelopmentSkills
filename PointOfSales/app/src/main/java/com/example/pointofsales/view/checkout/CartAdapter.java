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
import com.example.pointofsales.model.Cart;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.viewmodel.CheckoutViewModel;
import com.example.pointofsales.viewmodel.ProductViewModel;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private CheckoutViewModel mCheckoutViewModel;

    public class CartHolder extends RecyclerView.ViewHolder {

        private View mRootView;
        private TextView mTvProductName;
        private TextView mTvProductPrice;
        private Button mBtnMinusProductQuantity;
        private TextView mTvProductQuantity;
        private Button mBtnAddProductQuantity;
        private TextView mTvProductPriceExtension;

        public CartHolder(@NonNull View itemView) {
            super(itemView);

            mRootView = itemView;
            mTvProductName = itemView.findViewById(R.id.tvProductName);
            mTvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            mBtnMinusProductQuantity = itemView.findViewById(R.id.btnMinusProductQuantity);
            mTvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            mBtnAddProductQuantity = itemView.findViewById(R.id.btnAddProductQuantity);
            mTvProductPriceExtension = itemView.findViewById(R.id.tvProductPriceExtension);
        }

        public void bindCart(Cart cart, final int position) {
            mTvProductName.setText(cart.getProduct().getName());
            mTvProductPrice.setText(String.format("%.2f", cart.getProduct().getPrice()));
            mTvProductQuantity.setText(String.valueOf(cart.getCartQuantity()));
            mTvProductPriceExtension.setText(String.format("%.2f", cart.getCartExtension()));

            mBtnAddProductQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCheckoutViewModel.addCartQuantity(position);
                }
            });

            mBtnMinusProductQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCheckoutViewModel.minusCartQuantity(position);
                }
            });
        }
    }

    public CartAdapter(Context context, CheckoutViewModel checkoutViewModel) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mCheckoutViewModel = checkoutViewModel;
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartHolder(mLayoutInflater.inflate(R.layout.cart_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {
        holder.bindCart(mCheckoutViewModel.getCartItems().getValue().get(position), position);
    }

    @Override
    public int getItemCount() {
        return mCheckoutViewModel.getCartItems().getValue().size();
    }
}