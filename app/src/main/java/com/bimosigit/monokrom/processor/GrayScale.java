package com.bimosigit.monokrom.processor;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.bimosigit.monokrom.util.BitmapConverter;

/**
 * Created by sigitbn on 10/21/17.
 */

public class GrayScale {

    public static byte[] getImageGrayScale(byte[] bytes) {
        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int index = 0; index < pixels.length; index++) {
            int pixel = pixels[index];
            int red = Color.red(pixel);
            int green = Color.green(pixel);
            int blue = Color.blue(pixel);

            int alpha = (int) (0.299 * red + 0.587 * green + 0.114 * blue);
            pixels[index] = Color.rgb(alpha, alpha, alpha);
        }

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

        return BitmapConverter.bitmap2ByteArray(bitmap);
    }

}
