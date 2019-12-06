package com.example.pointofsales.model.sort;

/**
 * Class to obtain the sorting method for the membership
 */
public class MembershipSort {

    private int index = 0;
    private MembershipSortType[] mMembershipSortTypes = MembershipSortType.values();

    public MembershipSortType next() {
        return mMembershipSortTypes[index++ % mMembershipSortTypes.length];
    }
}
