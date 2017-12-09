package com.bimosigit.monokrom.processor;

import android.graphics.Bitmap;

import com.bimosigit.monokrom.util.BitmapConverter;

/**
 * Created by sigitbn on 11/29/17.
 */

public class Thinning {
    public static byte[] thinning(byte[] bytes) {

        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();


        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        boolean[] hits = new boolean[pixels.length];

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        boolean isChanged = true;

        while (isChanged) {
            isChanged = false;
            // check condition
            for (int index = 0; index < pixels.length; index++) {
                int[] indexNeighbour = new int[9];
                int[] neighbour = new int[9];
                indexNeighbour[0] = index - width - 1;
                indexNeighbour[1] = index - width;
                indexNeighbour[2] = index - width + 1;
                indexNeighbour[3] = index - 1;
                indexNeighbour[4] = index;
                indexNeighbour[5] = index + 1;
                indexNeighbour[6] = index + width - 1;
                indexNeighbour[7] = index + width;
                indexNeighbour[8] = index + width + 1;
            }
        }

        return BitmapConverter.bitmap2ByteArray(bitmap);
    }
}