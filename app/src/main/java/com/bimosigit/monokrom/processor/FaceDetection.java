package com.bimosigit.monokrom.processor;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.bimosigit.monokrom.model.Component;
import com.bimosigit.monokrom.model.Person;
import com.bimosigit.monokrom.util.BitmapConverter;

import java.util.List;

/**
 * Created by sigitbn on 10/23/17.
 */

public class FaceDetection {

    public static Person recognize(byte[] bytes, List<Component> components) {

        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);

        int LEFT_EYE_INDEX = 2;
        int RIGHT_EYE_INDEX = 3;
        int NOSE_INDEX = 4;

        Component leftEye = components.get(LEFT_EYE_INDEX);
        Component rightEye = components.get(RIGHT_EYE_INDEX);
        Component nose = components.get(NOSE_INDEX);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        int eyesDistance = Math.abs(rightEye.getCentroid() % width - leftEye.getCentroid() % width);
        int eyeNoseDistance = Math.abs(nose.getCentroid() - leftEye.getCentroid()) / width;


        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int startingPoint = leftEye.getCentroid() % width > rightEye.getCentroid() % width ?
                rightEye.getCentroid() : leftEye.getCentroid();

        for (int i = startingPoint; i < eyesDistance + startingPoint; i++) {
            pixels[i] = Color.rgb(255, 0, 0);
        }
        for (int i = 0; i < eyeNoseDistance; i++) {
            int index = startingPoint + (eyesDistance / 2) + (i * width);

            if (index < pixels.length)
                pixels[index] = Color.rgb(255, 0, 0);
        }

        double golden = (double) eyesDistance / (double) eyeNoseDistance;

        Log.d("golden", golden + "");

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        Person person = new Person();
        person.setBytes(BitmapConverter.bitmap2ByteArray(bitmap));
        person.setGoldenRatio(golden);

        return person;
    }
}
