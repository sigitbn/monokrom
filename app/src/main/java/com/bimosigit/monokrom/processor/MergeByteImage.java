package com.bimosigit.monokrom.processor;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.bimosigit.monokrom.constant.MonokromConstant;
import com.bimosigit.monokrom.util.BitmapConverter;

/**
 * Created by sigitbn on 10/22/17.
 */

public class MergeByteImage {

    public static byte[] merge(byte[] bytes, int[] componentPixels) {
        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pixelCount = width * height;

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < pixelCount; i++) {
            if (pixels[i] == MonokromConstant.PIXEL_WHITE || componentPixels[i] == MonokromConstant.PIXEL_WHITE) {
                pixels[i] = MonokromConstant.PIXEL_WHITE;
            }
            if (pixels[i] == Color.rgb(255, 0, 0) || componentPixels[i] == Color.rgb(255, 0, 0)) {
                pixels[i] = Color.rgb(255, 0, 0);
            }
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return BitmapConverter.bitmap2ByteArray(bitmap);
    }

    public static byte[] getBlackImage(byte[] bytes) {
        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pixelCount = width * height;

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < pixelCount; i++) {
            pixels[i] = MonokromConstant.PIXEL_BLACK;
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return BitmapConverter.bitmap2ByteArray(bitmap);
    }

    public static byte[] drawCentroid(byte[] bytes, int indexCentroid) {
        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pixelCount = width * height;

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int counter = 1; counter < 50; counter++) {

            if (indexCentroid + counter > 0 && indexCentroid + counter < pixelCount)
                pixels[indexCentroid + counter] = Color.rgb(255, 0, 0);

            if (indexCentroid - counter > 0 && indexCentroid - counter < pixelCount)
                pixels[indexCentroid - counter] = Color.rgb(255, 0, 0);

            if (indexCentroid + width * counter > 0 && indexCentroid + width * counter < pixelCount)
                pixels[indexCentroid + width * counter] = Color.rgb(255, 0, 0);

            if (indexCentroid - width * counter > 0 && indexCentroid - width * counter < pixelCount)
                pixels[indexCentroid - width * counter] = Color.rgb(255, 0, 0);
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return BitmapConverter.bitmap2ByteArray(bitmap);
    }

//    public static byte[] printComponent() {
//
//        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        int pixelCount = width * height;
//
//        int[] pixels = new int[width * height];
//
//        bitmap.setPixels(component.getComponentPixels(), 0, width, 0, 0, width, height);
//        bytesList.add(BitmapConverter.bitmap2ByteArray(bitmap));
//
//    }
}
