package com.example.pointofsales.model.sort;

public class MembershipSort {

    private int index = 0;
    private MembershipSortType[] mMembershipSortTypes = MembershipSortType.values();

    public MembershipSortType next() {
        return mMembershipSortTypes[index++ % mMembershipSortTypes.length];
    }
}
