package com.bimosigit.monokrom.processor;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.bimosigit.monokrom.util.BitmapConverter;

/**
 * Created by sigitbn on 10/22/17.
 */

public class BlackWhite {
    public static byte[] getBlackWhite(byte[] bytes, int threshold) {
        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < pixels.length; i++) {
            int pixel = Color.green(pixels[i]) > threshold ? 0 : 255;
            pixels[i] = Color.rgb(pixel, pixel, pixel);
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return BitmapConverter.bitmap2ByteArray(bitmap);
    }
}
