package com.example.pointofsales.view.product;

import android.widget.Filter;

import com.example.pointofsales.model.Product;
import com.example.pointofsales.viewmodel.ProductViewModel;

import java.util.ArrayList;

/**
 * ProductFilter class to filter the Products in the Product RecyclerView
 */
public class ProductFilter extends Filter {

    private ProductViewModel mProductViewModel;
    private ProductAdapter mAdapter;

    public ProductFilter(ProductAdapter productAdapter, ProductViewModel productViewModel) {
        mAdapter = productAdapter;
        mProductViewModel = productViewModel;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        // Get the query string
        String searchString = constraint.toString();
        ArrayList<Product> allProducts = mProductViewModel.getProductList().getValue();
        ArrayList<Product> filteredProducts = new ArrayList<>();

        if (searchString.isEmpty())
            filteredProducts = allProducts;
        else
            for (Product product : allProducts)
                // If the product name or price contains the query string, add it to the filtredProduct list
                if (product.getName().toLowerCase().contains(searchString.toLowerCase()) ||
                        String.format("%.2f", product.getPrice()).contains(searchString))
                    filteredProducts.add(product);

        FilterResults filterResults = new FilterResults();
        filterResults.values = filteredProducts;

        // Return the result
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        @SuppressWarnings("unchecked")
        ArrayList<Product> products = (ArrayList<Product>) results.values;
        mAdapter.setProducts(products);

        // Notify the RecyclerView on the data changed
        mAdapter.notifyDataSetChanged();
    }
}
