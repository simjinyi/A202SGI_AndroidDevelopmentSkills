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
import com.example.pointofsales.viewmodel.ProductViewModel;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ProductViewModel mProductViewModel;

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

        public void bindCart(Product product, final int position) {

//            if (product.getCartQuantity() <= 0)
//                mRootView.setVisibility(View.GONE);

            mTvProductName.setText(product.getName());
            mTvProductPrice.setText(String.format("%.2f", product.getPrice()));
            mTvProductQuantity.setText(String.valueOf(product.getCartQuantity()));
            mTvProductPriceExtension.setText(String.format("%.2f", product.getCartExtension()));

            mBtnAddProductQuantity.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    mProductViewModel.addCartQuantity(mProductViewModel.getCartList().getValue().get(position));
                }
            });

            mBtnMinusProductQuantity.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    mProductViewModel.minusCartQuantity(mProductViewModel.getCartList().getValue().get(position));
                }
            });
        }
    }

    public CartAdapter(Context context, ProductViewModel productViewModel) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mProductViewModel = productViewModel;
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartHolder(mLayoutInflater.inflate(R.layout.cart_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {
        holder.bindCart(mProductViewModel.getCartList().getValue().get(position), position);
    }

    @Override
    public int getItemCount() {
        return mProductViewModel.getCartList().getValue().size();
    }
}