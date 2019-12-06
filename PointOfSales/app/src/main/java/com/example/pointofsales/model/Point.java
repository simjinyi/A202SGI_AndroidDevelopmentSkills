package com.example.pointofsales.model;

import java.util.Comparator;

/**
 * Model class to store the membership point details
 */
public class Point {

    private String pointId;
    private String mUserName;
    private String mUserId;
    private String mStoreName;
    private String mStoreId;
    private String mStoreAddress;
    private int mStorePointsPerPrice;
    private int mPoints;

    public Point() {
        mUserName = mUserId = mStoreName = mStoreId = null;
        mPoints = 0;
    }

    public Point(String userName, String userId, String storeName, String storeId, String storeAddress, int storePointsPerPrice, int points) {
        mUserName = userName;
        mUserId = userId;
        mStoreName = storeName;
        mStoreId = storeId;
        mStoreAddress = storeAddress;
        mStorePointsPerPrice = storePointsPerPrice;
        mPoints = points;
    }

    // Comparator used in sorting the membership points
    // Store Name (Ascending)
    public static Comparator<Point> storeNameAsc = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            return o1.getStoreName().toLowerCase().compareTo(o2.getStoreName().toLowerCase());
        }
    };

    // Store Name (Descending)
    public static Comparator<Point> storeNameDesc = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            return o2.getStoreName().toLowerCase().compareTo(o1.getStoreName().toLowerCase());
        }
    };

    // Customer Name (Ascending)
    public static Comparator<Point> customerNameAsc = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            return o1.getUserName().toLowerCase().compareTo(o2.getUserName().toLowerCase());
        }
    };

    // Customer Name (Descending)
    public static Comparator<Point> customerNameDesc = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            return o2.getUserName().toLowerCase().compareTo(o1.getUserName().toLowerCase());
        }
    };

    // Membership Points (Ascending)
    public static Comparator<Point> pointAsc = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            return Integer.compare(o1.getPoints(), o2.getPoints());
        }
    };

    // Membership Points (Descending)
    public static Comparator<Point> pointDesc = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            return Integer.compare(o2.getPoints(), o1.getPoints());
        }
    };

    // Points Per Price (Ascending)
    public static Comparator<Point> pointsPerPriceAsc = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            return Integer.compare(o1.getStorePointsPerPrice(), o2.getStorePointsPerPrice());
        }
    };

    // Points Per Price (Descending)
    public static Comparator<Point> pointsPerPriceDesc = new Comparator<Point>() {
        @Override
        public int compare(Point o1, Point o2) {
            return Integer.compare(o2.getStorePointsPerPrice(), o1.getStorePointsPerPrice());
        }
    };
    // END Comparators

    public String getPointId() {
        return pointId;
    }
    public void setPointId(String pointId) {
        this.pointId = pointId;
    }

    public String getUserName() {
        return mUserName;
    }
    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getStoreName() {
        return mStoreName;
    }
    public void setStoreName(String storeName) {
        mStoreName = storeName;
    }

    public String getUserId() {
        return mUserId;
    }
    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getStoreId() {
        return mStoreId;
    }
    public void setStoreId(String storeId) {
        mStoreId = storeId;
    }

    public String getStoreAddress() {
        return mStoreAddress;
    }
    public void setStoreAddress(String storeAddress) {
        mStoreAddress = storeAddress;
    }

    public int getStorePointsPerPrice() {
        return mStorePointsPerPrice;
    }
    public void setStorePointsPerPrice(int storePointsPerPrice) {
        mStorePointsPerPrice = storePointsPerPrice;
    }

    public int getPoints() {
        return mPoints;
    }
    public void setPoints(int points) {
        mPoints = points;
    }
}
