package com.example.pointofsales.controller.product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pointofsales.R;
import com.example.pointofsales.controller.ProductViewModel;
import com.example.pointofsales.model.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductItemViewHolder> {

    private Context mContext;
    private ProductViewModel mProductViewModel;
    private LayoutInflater mLayoutInflater;

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
            mTvProductInventoryQuantity = itemView.findViewById(R.id.etProductInventoryQuantity);
            mTvProductName = itemView.findViewById(R.id.etProductName);
            mTvProductPrice = itemView.findViewById(R.id.etProductPrice);
            mBtnMinusProductQuantity = itemView.findViewById(R.id.btnMinusProductQuantity);
            mTvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            mBtnAddProductQuantity = itemView.findViewById(R.id.btnAddProductQuantity);
            mIbEditProduct = itemView.findViewById(R.id.ibEditProductImage);
        }

        public void bindProduct(Product product, final int position) {

            if (product.getImage() != null)
                mIvProductImage.setImageBitmap(product.getImage());

            mTvProductInventoryQuantity.setText(String.valueOf(product.getInventoryQuantity()));
            mTvProductName.setText(product.getName());
            mTvProductPrice.setText(mContext.getResources().getString(R.string.tvProductPrice, product.getPrice()));
            mTvProductQuantity.setText(String.valueOf(product.getCartQuantity()));

            mBtnAddProductQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Product pointedProduct = mProductViewModel.getProducts().getValue().getProductByIndex(position);

                    if (pointedProduct.getCartQuantity() + 1 <= pointedProduct.getInventoryQuantity()) {
                        pointedProduct.setCartQuantity(pointedProduct.getCartQuantity() + 1);
                        mProductViewModel.setProductCartQuantity(pointedProduct.getCartQuantity(), position);
                        mTvProductQuantity.setText(String.valueOf(pointedProduct.getCartQuantity()));
                    }
                }
            });

            mBtnMinusProductQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Product pointedProduct = mProductViewModel.getProducts().getValue().getProductByIndex(position);

                    if (pointedProduct.getCartQuantity() > 0) {
                        pointedProduct.setCartQuantity(pointedProduct.getCartQuantity() - 1);
                        mProductViewModel.setProductCartQuantity(pointedProduct.getCartQuantity(), position);
                        mTvProductQuantity.setText(String.valueOf(pointedProduct.getCartQuantity()));
                    }
                }
            });
        }
    }

    public ProductAdapter(Context context, View view, ProductViewModel productViewModel) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mProductViewModel = productViewModel;
    }

    @NonNull
    @Override
    public ProductItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductItemViewHolder(mLayoutInflater.inflate(R.layout.product_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductItemViewHolder holder, int position) {
        holder.bindProduct(mProductViewModel.getProducts().getValue().getProductByIndex(position), position);
    }

    @Override
    public int getItemCount() {
        return mProductViewModel.getProducts().getValue().getProductListSize();
    }

    @Override
    public long getItemId(int position) {
        return mProductViewModel.getProducts().getValue().getProductByIndex(position).hashCode();
    }
}
