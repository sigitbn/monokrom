package com.bimosigit.monokrom.processor;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.bimosigit.monokrom.util.BitmapConverter;

/**
 * Created by sigitbn on 10/21/17.
 */

public class Equalization {
    public static byte[] getImageEqualization(byte[] bytes) {
        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pixelCount = width * height;

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int[] histogram = Histogram.getHistogram(bytes);
        int[] lut = new int[histogram.length];

        int sum = 0;
        for (int i = 0; i < histogram.length; i++) {
            sum += histogram[i];
            lut[i] = sum * histogram.length / pixels.length;
        }

        for (int i = 0; i < pixelCount; i++) {
            int alpha = Color.green(pixels[i]);
            pixels[i] = Color.rgb(lut[alpha], lut[alpha], lut[alpha]);
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return BitmapConverter.bitmap2ByteArray(bitmap);
    }
}

