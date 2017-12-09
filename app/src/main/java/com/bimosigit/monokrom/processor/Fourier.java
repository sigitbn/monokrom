package com.bimosigit.monokrom.processor;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.bimosigit.monokrom.util.BitmapConverter;

//import org.apache.commons.math3.complex.Complex;

/**
 * Created by sigitbn on 11/18/17.
 */

public class Fourier {

    public static byte[] getBytesFromFFT(byte[] bytes) {
        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];

        Complex[][] domainFreq = fft(bytes);
        double max = 0;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                max = Math.max(max, domainFreq[i][j].abs());
            }
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int index = i + j * width;
//                int val = (int) Math.sqrt(Math.pow(domainFreq[i][j].getReal(), 2) + Math.pow(domainFreq[i][j].getImaginary(), 2)) * 255;

                int val = (int) (Math.log(domainFreq[i][j].abs() + 1) * 255 / Math.log(max + 1));
                pixels[index] = Color.rgb(val, val, val);
            }
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return BitmapConverter.bitmap2ByteArray(bitmap);
    }

    public static Complex[][] fft(byte[] bytes) {
        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Complex[][] domainFreq = new Complex[width][height];

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int freq = 0;
                Complex sum = new Complex(Color.green(pixels[x + y * width]), 0);

//                Complex sum = new Complex(0, 0);

                int max = 0;
                for (int m = 0; m < width; m++) {
                    double mPerWidth = (double) m / (double) width;

                    for (int n = 0; n < height; n++) {
                        int index = m + n * width;

                        double nPerHeight = (double) n / (double) height;
                        double val = 2.0 * Math.PI * ((double) x * mPerWidth + (double) y * nPerHeight) * -1.0;
//                        Complex complex = new Complex(val).exp().multiply(Color.green(pixels[index]));
                        Complex exp = new Complex(Math.cos(val), Math.sin(val));
//                        exp = new Complex(exp.re() * Color.green(pixels[index]), exp.im() );
                        Complex current = new Complex(Color.green(pixels[m + n * width]), 0);
//                        freq += (Color.green(pixels[index]) * complex.exp().getReal());
//                        double re = complex.exp().multiply(Color.green(pixels[index])).getReal();
                        current = current.times(exp);
//                        sum = new Complex(sum.re() + current.re() * exp.re() + current.im() * exp.im(),
//                                sum.im() - current.im() * exp.re() + current.re() * exp.im());
                        sum = sum.plus(current);

//                        Log.d("Exp:", "" + re);
//                        Log.d("Sum: real:" + sum.getReal() + ", im: " + sum.getImaginary(),
//                                "Complex: real:" + complex.getReal() + ", im: " + complex.getImaginary());
//                        freq += Color.green(pixels[index]) * Math.pow(1.0, (x * mPerWidth + y * nPerHeight));
//                        Log.d("Exp imajiner", complex.exp().getReal() + ", " + complex.exp().getImaginary() + ", " + val);
                    }
                }

                domainFreq[x][y] = sum;
//                domainFreq[width][height] = Math.max(domainFreq[width][height], freq);
//                getDomainFreq(x, y, width, height, pixels);
            }
        }
        return domainFreq;
    }

    private static int getDomainFreq(int x, int y, int width, int height, int[] pixels) {
        int freq = 0;
        for (int m = 0; m < width; m++) {
            double mPerWidth = m / width;

            for (int n = 0; n < height; n++) {
                int index = m + n * width;
                Complex complex = new Complex(pixels[index], 0);

                double nPerHeight = n / height;
                double val = complex.im() * 2 * Math.PI * (x * mPerWidth + y * nPerHeight);
                freq += (Color.green(pixels[index]) * Math.exp(val));
            }
        }
        return freq;
    }
}