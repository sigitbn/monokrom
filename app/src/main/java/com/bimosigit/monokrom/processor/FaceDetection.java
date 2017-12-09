package com.bimosigit.monokrom.processor;


import android.graphics.Bitmap;
import android.graphics.Color;

import com.bimosigit.monokrom.constant.MonokromConstant;
import com.bimosigit.monokrom.model.Component;
import com.bimosigit.monokrom.model.Person;
import com.bimosigit.monokrom.util.BitmapConverter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by sigitbn on 10/23/17.
 */

public class FaceDetection {

    public static List<byte[]> getFaces(byte[] bytes) {
        List<byte[]> faces = new ArrayList<>();
        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(bytes);

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < pixels.length; i++) {
            if (SkinFilter.isSkin(pixels[i])) {
                pixels[i] = MonokromConstant.PIXEL_WHITE;
            } else {
                pixels[i] = MonokromConstant.PIXEL_BLACK;
            }
        }
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        faces.add(BitmapConverter.bitmap2ByteArray(bitmap));
        return faces;
    }

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

        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        Person person = new Person();
        person.setComponents(components);
        person.setBytes(BitmapConverter.bitmap2ByteArray(bitmap));
        person.setWidth(width);
        person.setHeight(height);
        person.setGoldenRatio(golden);

        return person;
    }

    public static Person compare(Person dataTest, Person model) {
        int widthData = dataTest.getWidth();
        int heightData = dataTest.getHeight();

        int widthModel = model.getWidth();
        int heightModel = model.getHeight();

        List<Component> dataTestComponents = dataTest.getComponents();
        List<Component> coba = dataTestComponents;
        List<Component> modelComponents = model.getComponents();
//        Log.d("Nama ", model.getName() == null ? "Null" : model.getName());
        int totalMinimum = 0;

        for (int dataComponentIndex = 0; dataComponentIndex < dataTestComponents.size(); dataComponentIndex++) {
            // Get longest distance for width and height, set the longest distance as
            // width and height for new Canvas
            Component dataComponent = dataTestComponents.get(dataComponentIndex);
            int dataCentroidX = dataComponent.getCentroid() % widthData;
            int dataCentroidY = dataComponent.getCentroid() / widthData;

            int dataDistanceLeftX = dataCentroidX - dataComponent.getMinX();
            int dataDistanceRightX = dataComponent.getMaxX() - dataCentroidX;

            int dataDistanceLeftY = dataCentroidY - dataComponent.getMinY();
            int dataDistanceRightY = dataComponent.getMaxY() - dataCentroidY;

            int maxWidthData = dataDistanceLeftX > dataDistanceRightX ?
                    dataDistanceLeftX : dataDistanceRightX;

            int maxHeightData = dataDistanceLeftY > dataDistanceRightY ?
                    dataDistanceLeftY : dataDistanceRightY;

            int minimumDiff = widthData * heightData;
            if (modelComponents != null)
                for (int componentIndex = 0; componentIndex < modelComponents.size(); componentIndex++) {
                    Component modelComponent = modelComponents.get(componentIndex);
                    int modelCentroidX = modelComponent.getCentroid() % widthModel;
                    int modelCentroidY = modelComponent.getCentroid() / widthModel;

                    int modelDistanceLeftX = modelCentroidX - modelComponent.getMinX();
                    int modelDistanceRightX = modelComponent.getMaxX() - modelCentroidX;

                    int modelDistanceLeftY = modelCentroidY - modelComponent.getMinY();
                    int modelDistanceRightY = modelComponent.getMaxY() - modelCentroidY;

                    int maxWidthModel = modelDistanceLeftX > modelDistanceRightX ?
                            modelDistanceLeftX : modelDistanceRightX;

                    int maxHeightModel = modelDistanceLeftY > modelDistanceRightY ?
                            modelDistanceLeftY : modelDistanceRightY;

                    int canvasWidth = maxWidthData > maxWidthModel ? maxWidthData * 2 : maxWidthModel * 2;
                    int canvasHeight = maxHeightData > maxHeightModel ? maxHeightData * 2 : maxHeightModel * 2;

                    int canvasCentroidX = canvasWidth / 2;
                    int canvasCentroidY = canvasHeight / 2;
                    int[][] canvas = new int[canvasWidth + 10][canvasHeight + 10];
                    int[][] canvasModel = new int[canvasWidth + 10][canvasHeight + 10];

                    int distanceData2CanvasX = dataCentroidX - canvasCentroidX;
                    int distanceData2CanvasY = dataCentroidY - canvasCentroidY;

                    int distanceModel2CanvasX = modelCentroidX - canvasCentroidX;
                    int distanceModel2CanvasY = modelCentroidY - canvasCentroidY;

                    int[] dataPixels = dataComponent.getComponentPixels();
                    int[] modelPixels = modelComponent.getComponentPixels();

                    if (dataPixels == null) {
                        return dataTest;
                    }
                    for (int index = 0; index < dataPixels.length; index++) {
                        if (dataPixels[index] == MonokromConstant.PIXEL_WHITE) {
                            int x = index % widthData - distanceData2CanvasX;
                            int y = index / widthData - distanceData2CanvasY;
                            if (!(x > canvasWidth - 10) && !(y > canvasHeight - 10)) {
                                canvas[x][y] = 1;
                            }
                        }
                    }

//                    for (int index = 0; index < modelPixels.length; index++) {
//                        if (modelPixels[index] == MonokromConstant.PIXEL_WHITE) {
//                            int x = index % widthModel - distanceModel2CanvasX;
//                            int y = index / widthModel - distanceModel2CanvasY;
//                            if (x >= canvasWidth + 10 || y >= canvasHeight + 10) {
//                                Log.d("canvasWidth:" + canvasWidth + ", x :" + x, "canvasHeight:" + canvasHeight + ",y: " + y);
//                            } else {
//                                canvasModel[x][y] = 1;
//                            }
//                        }
//                    }

                    int diff = 0;
                    Character current = '0';
                    int index = 0;
                    int totalPoint = 0;
                    for (Character character : modelComponent.getChainCode().toCharArray()) {
                        if (!current.equals(character)) {
                            totalPoint++;
                            current = character;
                            int coordinate = modelComponent.getChainCodeCoordinate().get(index);
                            int x = coordinate % widthModel - distanceModel2CanvasX;
                            int y = coordinate / widthModel - distanceModel2CanvasY;
                            if (x < canvasWidth && y < canvasHeight) {

                                canvasModel[x][y] = 1;

                                int distX = x - canvasCentroidX;
                                int distY = y - canvasCentroidY;

                                // If x & y is in after of centroid, sign negative to step backward

                                int signX = distX > 0 ? -1 : 1;
                                int signY = distY > 0 ? -1 : 1;

                                int stepX = (int) Math.sqrt(distX * distX);
                                int stepY = (int) Math.sqrt(distY * distY);


                                if (stepX > stepY) {
                                    double stepValue = stepY / stepX;

//                                    if (canvas[x][y] == 1) {
//                                        for (int i = 1; i < stepX; i++) {
//                                            if (x + (i * signX * -1) >= 0 && (int) Math.floor(y + (i * stepValue * signY * -1)) >= 0) {
//                                                if (x + (i * signX * -1) < canvasWidth && (int) Math.floor(y + (i * stepValue * signY * -1)) < canvasHeight) {
//                                                    if (canvas[x + (i * signX * -1)][(int) Math.floor(y + (i * stepValue * signY * -1))] == 1) {
//                                                        diff++;
//                                                        int dataIndex = ((int) Math.floor(y + (i * stepValue * signY * -1)) + distanceData2CanvasY) * widthData
//                                                                + (x + (i * signX * -1) + distanceData2CanvasX);
//                                                        if (dataIndex < dataPixels.length) {
//                                                            dataPixels[dataIndex] = Color.rgb(255, 0, 0);
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    } else {
                                    for (int i = 0; i < stepX; i++) {
                                        if (x + (i * signX) >= 0 && (int) Math.floor(y + (i * stepValue * signY)) >= 0) {
                                            if (x + (i * signX) < canvasWidth && (int) Math.floor(y + (i * stepValue * signY)) < canvasHeight) {
                                                if (canvas[x + (i * signX)][(int) Math.floor(y + (i * stepValue * signY))] == 0) {
                                                    diff++;
                                                    int dataIndex = ((int) Math.floor(y + (i * stepValue * signY)) + distanceData2CanvasY) * widthData
                                                            + (x + (i * signX) + distanceData2CanvasX);
                                                    if (dataIndex < dataPixels.length) {
                                                        dataPixels[dataIndex] = Color.rgb(255, 0, 0);
                                                    }
                                                }
                                            }
                                        }
//                                        }
                                    }
                                } else {
                                    double stepValue = stepX / stepY;
//                                    if (canvas[x][y] == 1) {
//                                        for (int i = 1; i < stepY; i++) {
//                                            if ((int) (x + (i * stepValue * signY * -1)) >= 0 && y + (i * signY) * -1 >= 0) {
//                                                if ((int) (x + (i * stepValue * signY * -1)) < canvasWidth && y + (i * signY) * -1 < canvasHeight) {
//                                                    if (canvas[(int) (x + (i * stepValue * signY * -1))][y + (i * signY) * -1] == 1) {
//                                                        diff++;
//                                                        int dataIndex = (y + (i * signY) + distanceData2CanvasY) * widthData
//                                                                + ((int) (x + (i * stepValue * signY)) + distanceData2CanvasX);
//                                                        if (dataIndex < dataPixels.length) {
//                                                            Log.d("berhasil", "berhasil");
//                                                            dataPixels[dataIndex] = Color.rgb(255, 0, 0);
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    } else {
                                    for (int i = 0; i < stepY; i++) {
                                        if ((int) (x + (i * stepValue * signY)) >= 0 && y + (i * signY) >= 0) {
                                            if ((int) (x + (i * stepValue * signY)) < canvasWidth && y + (i * signY) < canvasHeight) {
                                                if (canvas[(int) (x + (i * stepValue * signY))][y + (i * signY)] == 0) {
                                                    diff++;
                                                    int dataIndex = (y + (i * signY) + distanceData2CanvasY) * widthData
                                                            + ((int) (x + (i * stepValue * signY)) + distanceData2CanvasX);
                                                    if (dataIndex < dataPixels.length) {
                                                        dataPixels[dataIndex] = Color.rgb(255, 0, 0);
                                                    }
                                                }
                                            }
//                                            }
                                        }
                                    }
                                }

                            }
                        }
                        index++;

                    }
                    diff = diff / totalPoint;
                    if (diff < minimumDiff) {
                        dataComponent.setComponentPixels(dataPixels);
                        minimumDiff = diff;
                        coba.set(dataComponentIndex, dataComponent);
                    }

//                    for (int[] c : canvasModel) {
//                        System.out.println(Arrays.toString(c));
//                    }
//                    for (int x = 0; x < canvasWidth + 10; x++) {
//                        for (int y = 0; y < canvasHeight + 10; y++) {
//                            diff = diff + (canvas[x][y] - canvasModel[x][y]) ^ 2;
//                        }
//                    }

                }
            totalMinimum += minimumDiff;
        }
        dataTest.setDistance(totalMinimum);
        dataTest.setComponents(coba);
        return dataTest;
    }

    private static Component mapPixel(Component modelComponent) {
        // TODO: 11/7/17 mapping ke canvas baru
        return new Component();
    }
}
