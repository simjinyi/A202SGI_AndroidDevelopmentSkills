package com.example.pointofsales.model;

public class ProductSort {

    private int index = 0;
    private ProductSortType[] mProductSortTypes = ProductSortType.values();

    public ProductSortType next() {
        return mProductSortTypes[index++ % mProductSortTypes.length];
    }
}
