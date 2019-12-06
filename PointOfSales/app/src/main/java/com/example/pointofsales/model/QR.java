package com.example.pointofsales.model;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

/**
 * Model class to generate the QR code from the userId
 */
public class QR {
    public static Bitmap generateQRCode(String data) {
        try {

            // Generate the 300x300 QR code from the given String (userId)
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(data, BarcodeFormat.QR_CODE,300,300);
            return new BarcodeEncoder().createBitmap(bitMatrix);

        } catch (WriterException e) {

            e.printStackTrace();
            return null;
        }
    }
}
