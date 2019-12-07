package com.example.pointofsales.view.checkout;

import com.example.pointofsales.model.Point;

/**
 * UpdatePointInterface to get notified on the membership details changed and the status of the deletion of the membership
 */
public interface UpdatePointInterface {
    void onPointChanged(Point point);
    void onPointDeleted();
}
