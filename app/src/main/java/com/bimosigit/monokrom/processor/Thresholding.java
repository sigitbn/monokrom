package com.bimosigit.monokrom.processor;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sigitbn on 10/21/17.
 */

public class Thresholding {
    public static List<Integer> getThreshold(byte[] bytes) {

        int[] histogram = Histogram.getHistogram(bytes);
        List<Integer> thresholds = new ArrayList<>();

        for (int i = 1; i < histogram.length - 1; i++) {
            int diffBefore = histogram[i - 1] - histogram[i];
            int diffAfter = histogram[i + 1] - histogram[i];
            if (diffAfter > 0 && diffBefore > 0) {
                thresholds.add(i);
            }
        }
        Log.d("Threshold", "" + thresholds.size());

        // TODO: 10/22/17 Blackwhite, getobject, floodfill, goldenration
        // // TODO: 10/22/17 Simpan object yang memenuhi syarat, kalau tidak, Hapus
        /*
        get histogram
        looping histogram
            cari lembah, cek before after

        looping lembah
            cek objeknya apakah 7
         */
        return thresholds;
    }
}
