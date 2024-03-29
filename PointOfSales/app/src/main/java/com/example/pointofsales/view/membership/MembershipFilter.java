package com.example.pointofsales.view.membership;

import android.util.Log;
import android.widget.Filter;

import com.example.pointofsales.model.Point;
import com.example.pointofsales.viewmodel.MembershipViewModel;

import java.util.ArrayList;

/**
 * MembershipFilter class filters the Membership in the RecyclerView
 */
public class MembershipFilter extends Filter {

    private MembershipViewModel mMembershipViewModel;
    private MembershipAdapter mMembershipAdapter;

    public MembershipFilter(MembershipViewModel membershipViewModel, MembershipAdapter membershipAdapter) {
        mMembershipViewModel = membershipViewModel;
        mMembershipAdapter = membershipAdapter;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        String searchString = constraint.toString();
        ArrayList<Point> points = mMembershipViewModel.getPoints().getValue();
        ArrayList<Point> filteredPoints = new ArrayList<>();

        if (searchString.isEmpty())
            filteredPoints = points;
        else
            for (Point point : points)
                // Check if the query string matches the user name, store name, store address, points or points per price
                if (point.getStoreName().toLowerCase().contains(searchString.toLowerCase()) ||
                        point.getUserName().toLowerCase().contains(searchString.toLowerCase()) ||
                        point.getStoreAddress().toLowerCase().contains(searchString.toLowerCase()) ||
                        searchString.toLowerCase().contains(String.valueOf(point.getPoints())) ||
                        searchString.toLowerCase().contains(String.valueOf(point.getStorePointsPerPrice())))

                    // Add the matching points
                    filteredPoints.add(point);

        FilterResults filterResults = new FilterResults();
        filterResults.values = filteredPoints;

        // Return the filter result
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        @SuppressWarnings("unchecked")
        ArrayList<Point> points = (ArrayList<Point>) results.values;
        mMembershipAdapter.setPoints(points);

        // Publish the result to the Membership RecyclerView
        mMembershipAdapter.notifyDataSetChanged();
    }
}
