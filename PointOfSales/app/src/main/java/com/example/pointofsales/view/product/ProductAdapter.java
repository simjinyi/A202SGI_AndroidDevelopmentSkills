package com.example.pointofsales.view.product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pointofsales.R;
import com.example.pointofsales.model.Product;
import com.example.pointofsales.view.OnSingleClickListener;
import com.example.pointofsales.viewmodel.ProductViewModel;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductItemViewHolder> implements Filterable {

    private Context mContext;
    private ProductViewModel mProductViewModel;
    private LayoutInflater mLayoutInflater;
    private EditButtonClick mEditButtonClick;
    private ArrayList<Product> mProducts;

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

        public void bindProduct(Product product, final int position) {

            if (product.getImage() != null)
                mIvProductImage.setImageBitmap(product.getImage());
            else
                mIvProductImage.setImageResource(R.drawable.ic_add_photo_alternate_24px);

            mTvProductInventoryQuantity.setText(String.valueOf(product.getInventoryQuantity()));
            mTvProductName.setText(product.getName());
            mTvProductPrice.setText(mContext.getResources().getString(R.string.tvProductPrice, product.getPrice()));
            mTvProductQuantity.setText(String.valueOf(product.getCartQuantity()));

            mBtnAddProductQuantity.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    mProductViewModel.addCartQuantity(mProducts.get(position));
                }
            });

            mBtnMinusProductQuantity.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    mProductViewModel.minusCartQuantity(mProducts.get(position));
                }
            });

            mIbEditProduct.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    mEditButtonClick.onEditButtonClick(mProducts.get(position));
                }
            });
        }
    }

    public ProductAdapter(Context context, EditButtonClick editButtonClick, ProductViewModel productViewModel) {
        mContext = context;
        mEditButtonClick = editButtonClick;
        mLayoutInflater = LayoutInflater.from(context);
        mProductViewModel = productViewModel;
        mProducts = mProductViewModel.getProductList().getValue();
    }

    @NonNull
    @Override
    public ProductItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductItemViewHolder(mLayoutInflater.inflate(R.layout.list_item_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductItemViewHolder holder, int position) {
        holder.bindProduct(mProducts.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    @Override
    public long getItemId(int position) {
        return mProducts.get(position).hashCode();
    }

    public void move(int fromPosition, int toPosition) {
        mProductViewModel.moveProduct(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public Filter getFilter() {
        return new ProductFilter(this, mProductViewModel);
    }

    public void setProducts(ArrayList<Product> products) {
        mProducts = products;
    }
}
