package com.example.pointofsales.view.product;

import android.widget.Filter;

import com.example.pointofsales.model.Product;
import com.example.pointofsales.viewmodel.ProductViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ProductFilter extends Filter {

    private ProductViewModel mProductViewModel;
    private ProductAdapter mAdapter;

    public ProductFilter(ProductAdapter productAdapter, ProductViewModel productViewModel) {
        mAdapter = productAdapter;
        mProductViewModel = productViewModel;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        String searchString = constraint.toString();
        ArrayList<Product> allProducts = mProductViewModel.getProductList().getValue();
        ArrayList<Product> filteredProducts = new ArrayList<>();

        if (searchString.isEmpty())
            filteredProducts = allProducts;
        else
            for (Product product : allProducts)
                if (product.getName().toLowerCase().contains(searchString.toLowerCase()) || String.valueOf(product.getPrice()).contains(searchString))
                    filteredProducts.add(product);

        FilterResults filterResults = new FilterResults();
        filterResults.values = filteredProducts;

        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        mAdapter.setProducts((ArrayList<Product>) results.values);
        mAdapter.notifyDataSetChanged();
    }
}
