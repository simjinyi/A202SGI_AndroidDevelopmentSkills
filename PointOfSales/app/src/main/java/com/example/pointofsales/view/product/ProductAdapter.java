package com.example.pointofsales.view.product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.viewmodel.CheckoutViewModel;
import com.example.pointofsales.viewmodel.ProductViewModel;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductItemViewHolder> {

    private Context mContext;
    private ProductViewModel mProductViewModel;
    private CheckoutViewModel mCheckoutViewModel;
    private LayoutInflater mLayoutInflater;
    private EditButtonClick mEditButtonClick;

    public class ProductItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView mIvProductImage;
        private TextView mTvProductInventoryQuantity;
        private TextView mTvProductName;
        private TextView mTvProductPrice;
        private Button mBtnMinusProductQuantity;
        private TextView mTvProductQuantity;
        private Button mBtnAddProductQuantity;
        private ImageButton mIbEditProduct;

        public ProductItemViewHolder(@NonNull View itemView) {
            super(itemView);

            mIvProductImage = itemView.findViewById(R.id.ivProductImage);
            mTvProductInventoryQuantity = itemView.findViewById(R.id.tvProductInventoryQuantity);
            mTvProductName = itemView.findViewById(R.id.tvProductName);
            mTvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            mBtnMinusProductQuantity = itemView.findViewById(R.id.btnMinusProductQuantity);
            mTvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            mBtnAddProductQuantity = itemView.findViewById(R.id.btnAddProductQuantity);
            mIbEditProduct = itemView.findViewById(R.id.ibEditProductImage);
        }

        public void bindProduct(Product product, int quantity, final int position) {

            if (product.getImage() != null)
                mIvProductImage.setImageBitmap(product.getImage());

            mTvProductInventoryQuantity.setText(String.valueOf(product.getInventoryQuantity()));
            mTvProductName.setText(product.getName());
            mTvProductPrice.setText(mContext.getResources().getString(R.string.tvProductPrice, product.getPrice()));
            mTvProductQuantity.setText(String.valueOf(quantity));

            mBtnAddProductQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCheckoutViewModel.addCartQuantity(mProductViewModel.getProductList().getValue().get(position));
                }
            });

            mBtnMinusProductQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCheckoutViewModel.minusCartQuantity(mProductViewModel.getProductList().getValue().get(position));
                }
            });

            mIbEditProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditButtonClick.onEditButtonClick(position);
                }
            });
        }
    }

    public ProductAdapter(Context context, EditButtonClick editButtonClick, ProductViewModel productViewModel, CheckoutViewModel checkoutViewModel) {
        mContext = context;
        mEditButtonClick = editButtonClick;
        mLayoutInflater = LayoutInflater.from(context);
        mProductViewModel = productViewModel;
        mCheckoutViewModel = checkoutViewModel;
    }

    @NonNull
    @Override
    public ProductItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductItemViewHolder(mLayoutInflater.inflate(R.layout.product_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductItemViewHolder holder, int position) {
        Product product = mProductViewModel.getProductList().getValue().get(position);
        holder.bindProduct(product, mCheckoutViewModel.getCartQuantity(product), position);
    }

    @Override
    public int getItemCount() {
        return mProductViewModel.getProductList().getValue().size();
    }

    @Override
    public long getItemId(int position) {
        return mProductViewModel.getProductList().getValue().get(position).hashCode();
    }
}
