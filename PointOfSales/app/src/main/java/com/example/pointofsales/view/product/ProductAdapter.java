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
import com.example.pointofsales.viewmodel.CheckoutViewModel;
import com.example.pointofsales.viewmodel.ProductViewModel;

import java.util.ArrayList;

/**
 * ProductAdapter to populate the product RecyclerView
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductItemViewHolder> implements Filterable {

    private Context mContext;
    private ProductViewModel mProductViewModel;
    private CheckoutViewModel mCheckoutViewModel;
    private LayoutInflater mLayoutInflater;
    private EditButtonClick mEditButtonClick;
    private ArrayList<Product> mProducts;

    /**
     * ProductViewHolder holds the product list item view
     */
    public class ProductItemViewHolder extends RecyclerView.ViewHolder {

        // View components
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

            // Assign reference to the view components
            mIvProductImage = itemView.findViewById(R.id.ivProductImage);
            mTvProductInventoryQuantity = itemView.findViewById(R.id.tvProductInventoryQuantity);
            mTvProductName = itemView.findViewById(R.id.tvProductName);
            mTvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            mBtnMinusProductQuantity = itemView.findViewById(R.id.btnMinusProductQuantity);
            mTvProductQuantity = itemView.findViewById(R.id.tvProductQuantity);
            mBtnAddProductQuantity = itemView.findViewById(R.id.btnAddProductQuantity);
            mIbEditProduct = itemView.findViewById(R.id.ibEditProductImage);
        }

        /**
         * Bind the product details to the view components
         * @param product product object containing the details to be bound
         * @param position position of the adapter
         */
        public void bindProduct(Product product, final int position) {

            // Set the image accordingly based on the image availability
            if (product.getImage() != null)
                mIvProductImage.setImageBitmap(product.getImage());
            else
                mIvProductImage.setImageResource(R.drawable.ic_add_photo_alternate_24px);

            mTvProductInventoryQuantity.setText(String.valueOf(product.getInventoryQuantity()));
            mTvProductName.setText(product.getName());
            mTvProductPrice.setText(mContext.getResources().getString(R.string.tvProductPrice, product.getPrice()));
            mTvProductQuantity.setText(String.valueOf(product.getCartQuantity()));

            // Add quantity clicked
            mBtnAddProductQuantity.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {

                    // Calls the ViewModels to update the product quantity and membership points
                    mProductViewModel.addCartQuantity(mProducts.get(position));
                    mCheckoutViewModel.updatePoint(null, false);
                }
            });

            // Minus quantity clicked
            mBtnMinusProductQuantity.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {

                    // Calls the ViewModels to update the product quantity and membership points
                    mProductViewModel.minusCartQuantity(mProducts.get(position));
                    mCheckoutViewModel.updatePoint(null, false);
                }
            });

            // Edit button clicked
            mIbEditProduct.setOnClickListener(new OnSingleClickListener() {
                @Override
                public void onSingleClick(View v) {

                    // Calls the EditButtonClick interface to pass back the product to be edited
                    mEditButtonClick.onEditButtonClick(mProducts.get(position));
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

    /**
     * Move the product items when the seller attempts reorganize the product page
     * @param fromPosition original position of the product object
     * @param toPosition position to be moved of the product object
     */
    public void move(int fromPosition, int toPosition) {
        mProductViewModel.moveProduct(fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * Filters the product items
     * @return ProductFilter object to filter the products based on the query string
     */
    @Override
    public Filter getFilter() {
        return new ProductFilter(this, mProductViewModel);
    }

    public void setProducts(ArrayList<Product> products) {
        mProducts = products;
    }
}
