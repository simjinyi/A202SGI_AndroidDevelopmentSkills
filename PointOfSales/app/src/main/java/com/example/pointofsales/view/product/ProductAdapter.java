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
import com.example.pointofsales.viewmodel.ProductViewModel;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductItemViewHolder> {

    private Context mContext;
    private ProductViewModel mProductViewModel;
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
                    mProductViewModel.setProductCartQuantity(mProductViewModel.getProductList().getValue().get(position).getCartQuantity() + 1, position);
                }
            });

            mBtnMinusProductQuantity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mProductViewModel.setProductCartQuantity(mProductViewModel.getProductList().getValue().get(position).getCartQuantity() - 1, position);
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

    public ProductAdapter(Context context, EditButtonClick editButtonClick, ProductViewModel productViewModel) {
        mContext = context;
        mEditButtonClick = editButtonClick;
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
        holder.bindProduct(mProductViewModel.getProductList().getValue().get(position), position);
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
