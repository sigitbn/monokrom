package com.bimosigit.monokrom.processor;

import android.graphics.Bitmap;

import com.bimosigit.monokrom.util.BitmapConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sigitbn on 10/29/17.
 */

public class SkinFilter {
    static List<int[]> skins;

    public static void setSkins(byte[] bytes) {
        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (skins == null) {
            skins = new ArrayList<>();
        }

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        skins.add(pixels);
    }

    public static boolean isSkin(int pixelChecked) {
        if (skins == null) return false;
        for (int[] skin : skins) {
            for (int pixel : skin) {
                if (pixelChecked == pixel) {
                    return true;
                }
            }
        }
        return false;
    }
}
