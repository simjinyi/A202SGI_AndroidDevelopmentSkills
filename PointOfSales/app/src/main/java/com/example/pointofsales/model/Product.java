package com.example.pointofsales.model;

import android.graphics.Bitmap;

import java.util.Comparator;

public class Product {

    private String mId;
    private Bitmap mImage;
    private String mName;
    private float mPrice;
    private int mPointPerItem;
    private int mInventoryQuantity;
    private int mTotalSales;
    private boolean mIsDisabled;
    private String mStoreId;

    private int mCartQuantity;
    private float mCartExtension;

    public static Comparator<Product> nameAscComparator = new Comparator<Product>() {
        @Override
        public int compare(Product product1, Product product2) {
            return product1.getName().toLowerCase().compareTo(product2.getName().toLowerCase());
        }
    };

    public static Comparator<Product> nameDescComparator = new Comparator<Product>() {
        @Override
        public int compare(Product product1, Product product2) {
            return product2.getName().toLowerCase().compareTo(product1.getName().toLowerCase());
        }
    };

    public static Comparator<Product> priceAscComparator = new Comparator<Product>() {
        @Override
        public int compare(Product product1, Product product2) {
            return Float.compare(product1.getPrice(), product2.getPrice());
        }
    };

    public static Comparator<Product> priceDescComparator = new Comparator<Product>() {
        @Override
        public int compare(Product product1, Product product2) {
            return Float.compare(product2.getPrice(), product1.getPrice());
        }
    };

    public static Comparator<Product> inventoryAscComparator = new Comparator<Product>() {
        @Override
        public int compare(Product product1, Product product2) {
            return Integer.compare(product1.getInventoryQuantity(), product2.getInventoryQuantity());
        }
    };

    public static Comparator<Product> inventoryDescComparator = new Comparator<Product>() {
        @Override
        public int compare(Product product1, Product product2) {
            return Integer.compare(product2.getInventoryQuantity(), product1.getInventoryQuantity());
        }
    };

    public static Comparator<Product> cartAscComparator = new Comparator<Product>() {
        @Override
        public int compare(Product product1, Product product2) {
            return Integer.compare(product1.getCartQuantity(), product2.getCartQuantity());
        }
    };

    public static Comparator<Product> cartDescComparator = new Comparator<Product>() {
        @Override
        public int compare(Product product1, Product product2) {
            return Integer.compare(product2.getCartQuantity(), product1.getCartQuantity());
        }
    };

    public Product() {
        mCartQuantity = 0;
    }

    public String getId() {
        return mId;
    }
    public void setId(String id) {
        if (id != null)
            mId = id;
    }

    public Bitmap getImage() {
        return mImage;
    }
    public void setImage(Bitmap image) {
        mImage = image;
    }

    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }

    public float getPrice() {
        return mPrice;
    }
    public void setPrice(float price) {
        if (price > 0)
            mPrice = price;
    }

    public int getPointPerItem() {
        return mPointPerItem;
    }
    public void setPointPerItem(int pointPerItem) {
        if (pointPerItem >= 0)
            mPointPerItem = pointPerItem;
    }

    public int getInventoryQuantity() {
        return mInventoryQuantity;
    }
    public void setInventoryQuantity(int inventoryQuantity) {
        if (inventoryQuantity >= 0)
            mInventoryQuantity = inventoryQuantity;
    }

    public int getTotalSales() {
        return mTotalSales;
    }
    public void setTotalSales(int totalSales) {
        mTotalSales = totalSales;
    }

    public boolean isDisabled() {
        return mIsDisabled;
    }
    public void setDisabled(boolean disabled) {
        mIsDisabled = disabled;
    }

    public int getCartQuantity() {
        return mCartQuantity;
    }
    public void setCartQuantity(int cartQuantity) {
        if (cartQuantity >= 0)
            mCartQuantity = cartQuantity;
    }

    public float getCartExtension() {
        return mCartExtension;
    }
    public void setCartExtension(float cartExtension) {
        mCartExtension = cartExtension;
    }

    public String getStoreId() {
        return mStoreId;
    }
    public void setStoreId(String storeId) {
        mStoreId = storeId;
    }
}
