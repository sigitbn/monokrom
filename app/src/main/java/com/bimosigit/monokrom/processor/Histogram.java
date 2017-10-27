package com.bimosigit.monokrom.processor;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.bimosigit.monokrom.util.BitmapConverter;

/**
 * Created by sigitbn on 10/21/17.
 */

public class Histogram {
    public static int[] getHistogram(byte[] bytes) {

        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        int[] histogram = new int[256];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int pixel : pixels) {
            int alpha = Color.green(pixel);
            histogram[alpha]++;
        }
        return histogram;
    }
}
