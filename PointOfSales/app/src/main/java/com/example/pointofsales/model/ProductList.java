package com.example.pointofsales.model;

import java.util.ArrayList;

public class ProductList {

    private ArrayList<Product> mProducts;

    public ProductList() {
        mProducts = new ArrayList<>();
    }

    public ArrayList<Product> getProducts() {
        return mProducts;
    }

    public void setProducts(ArrayList<Product> products) {
        mProducts = products;
    }

    public Product getProductById(String id) {
        for (Product product : mProducts)
            if (product.getId().equals(id))
                return product;

        return null;
    }

    public Product getProductByIndex(int index) {
        return mProducts.get(index);
    }

    public int getProductListSize() {
        return mProducts.size();
    }
}
