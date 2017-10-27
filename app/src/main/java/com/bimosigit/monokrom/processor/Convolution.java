package com.bimosigit.monokrom.processor;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.bimosigit.monokrom.util.BitmapConverter;

import java.util.Arrays;

/**
 * Created by sigitbn on 10/21/17.
 */

public class Convolution {
    public static byte[] getImageConvolution(byte[] bytes) {
        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int[] byteArrayGrayScale = BitmapConverter.bitmap2IntArrayGrayScale(bitmap);

        int[] newPixels = new int[pixels.length];

        for (int i = 0; i < pixels.length; i++) {
//            int median = kernel(pixels, i, width);
            int median = getMedian(byteArrayGrayScale, i, width);
            newPixels[i] = Color.rgb(median, median, median);
        }

        bitmap.setPixels(newPixels, 0, width, 0, 0, width, height);

        return BitmapConverter.bitmap2ByteArray(bitmap);
    }

    private static int getMedian(int[] pixels, int index, int width) {

        int median;
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

        int sum = 0;
        for (int i = 0; i < indexNeighbour.length; i++) {

            if (indexNeighbour[i] < 0 || indexNeighbour[i] >= pixels.length) {
                neighbour[i] = 0;
            } else {
                neighbour[i] = pixels[indexNeighbour[i]];
            }
//            sum += neighbour[i];
        }

        Arrays.sort(neighbour);
        median = neighbour[4];
        return median;
    }

    private static int kernel(int[] pixels, int index, int width) {

        int mean;
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

        int sum = 0;
        for (int i = 0; i < indexNeighbour.length; i++) {

            if (indexNeighbour[i] < 0 || indexNeighbour[i] >= pixels.length) {
                neighbour[i] = 0;
            } else {
                neighbour[i] = pixels[indexNeighbour[i]];
            }
            sum += neighbour[i];
        }

        mean = sum / 9;
        return mean;
    }
}
