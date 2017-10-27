package com.bimosigit.monokrom.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.io.ByteArrayOutputStream;

/**
 * Created by sigitbn on 10/21/17.
 */

public class BitmapConverter {

    public static Bitmap byteArray2Bitmap(byte[] bytes) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    public static byte[] bitmap2ByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    public static int[] bitmap2IntArrayGrayScale(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pixelCount = width * height;

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int[] intGrayScale = new int[pixelCount];
        for (int i = 0; i < pixelCount; i++) {

            intGrayScale[i] = Color.green(pixels[i]);
        }
        return intGrayScale;
    }
}
