package com.bimosigit.monokrom.processor;

import android.graphics.Bitmap;
import android.util.Log;

import com.bimosigit.monokrom.constant.MonokromConstant;
import com.bimosigit.monokrom.model.Component;
import com.bimosigit.monokrom.util.BitmapConverter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by sigitbn on 10/21/17.
 */

public class ChainCode {

    public static ArrayList<Component> getComponents(byte[] blackWhiteImage) {
        ArrayList<Component> components = new ArrayList<>();
        Bitmap bitmap = BitmapConverter.byteArray2Bitmap(blackWhiteImage);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();


        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        int index = 0;
        boolean[] hits = new boolean[pixels.length];
        boolean bad = false;

        while (index < pixels.length) {
            if (pixels[index] == MonokromConstant.PIXEL_WHITE && !hits[index]) {

                String chainCode = getChainCode(index, pixels, width);

                Queue<Integer> queue = new LinkedList<Integer>();
                queue.add(index);

                int minimumIndex = pixels.length;
                int maximumIndex = 0;

                int minimumX = pixels.length;
                int maximumX = 0;
                int weight_x = 0;
                int weight_y = 0;
                int totalPixel = 0;
                int[] componentPixels = new int[pixels.length];

                while (!queue.isEmpty()) {
                    int removedIndex = queue.remove();

                    if (floodFillImageDo(hits, removedIndex, MonokromConstant.PIXEL_WHITE, MonokromConstant.PIXEL_BLACK, pixels)) {

                        hits[removedIndex] = true;
                        pixels[removedIndex] = MonokromConstant.PIXEL_BLACK;
                        componentPixels[removedIndex] = MonokromConstant.PIXEL_WHITE;

                        minimumIndex = removedIndex < minimumIndex ? removedIndex : minimumIndex;
                        maximumIndex = removedIndex > maximumIndex ? removedIndex : maximumIndex;

                        minimumX = removedIndex % width < minimumX ? removedIndex % width : minimumX;
                        maximumX = removedIndex % width > maximumX ? removedIndex % width : maximumX;

                        queue.add(removedIndex - width);
                        queue.add(removedIndex + width);

                        if (removedIndex % width != 0) {
                            queue.add(removedIndex - 1);
                            queue.add(removedIndex - width - 1);
                            queue.add(removedIndex + width - 1);
                        }

                        if (removedIndex % width != width - 1) {
                            queue.add(removedIndex + 1);
                            queue.add(removedIndex - width + 1);
                            queue.add(removedIndex + width + 1);
                        }

                        totalPixel++;
                        weight_x += removedIndex % width;
                        weight_y += removedIndex / width;
                    }

                }

                int centroid_x = weight_x / totalPixel;
                int centroid_y = weight_y / totalPixel;

                int centroid = (centroid_y * width + centroid_x);

                int componentHeight = (maximumIndex / width - minimumIndex / width);
                int componentLength = (maximumX - minimumX);

                if (componentHeight > 26 && componentLength > 60) {
                    Log.d("X Y", componentHeight + "," + componentLength);
                    Component component = new Component(componentPixels, chainCode, centroid);
                    components.add(component);
                }
            }
            index++;
        }
        return components;
    }


    private static boolean floodFillImageDo(boolean[] hits, int index, int srcColor, int tgtColor, int[] pixels) {
        if (index < 0) return false;
        if (index > pixels.length - 1) return false;

        if (hits[index]) return false;

        if (pixels[index] != srcColor)
            return false;

        if (pixels[index] == tgtColor)
            return false;

        return true;
    }

    private static String getChainCode(int firstPixelIndex, int[] pixels, int width) {
        StringBuilder chainCodeString = new StringBuilder();
        int nextDirection = 3;
        int currentPixel = firstPixelIndex;
        boolean isFirst = true;

        while (currentPixel != firstPixelIndex || isFirst) {

            isFirst = false;

            // Direction of chain code
            int topRight = currentPixel - width + 1;
            int right = currentPixel + 1;
            int bottomRight = currentPixel + width + 1;
            int bottom = currentPixel + width;
            int bottomLeft = currentPixel + width - 1;
            int left = currentPixel - 1;
            int topLeft = currentPixel - width - 1;
            int top = currentPixel - width;

            int topRightDiffX = Math.abs((topRight % width) - (currentPixel % width));
            int rightDiffX = Math.abs((right % width) - (currentPixel % width));
            int bottomRightDiffX = Math.abs((bottomRight % width) - (currentPixel % width));
            int bottomDiffX = Math.abs((bottom % width) - (currentPixel % width));
            int bottomLeftDiffX = Math.abs((bottomLeft % width) - (currentPixel % width));
            int leftDiffX = Math.abs((left % width) - (currentPixel % width));
            int topLeftDiffX = Math.abs((topLeft % width) - (currentPixel % width));
            int topDiffX = Math.abs((top % width) - (currentPixel % width));

            switch (nextDirection) {

                // If pixel found -> break, else -> continue next case, check all 8 direction
                case 3:
                    if (topRight >= 0 && topRight < pixels.length
                            && pixels[topRight] == MonokromConstant.PIXEL_WHITE
                            && topRightDiffX == 1) { //top right , chaincode = 3

                        nextDirection = 9;
                        currentPixel = topRight;
                        chainCodeString.append(3);
                        break;
                    } else if (right >= 0 && right < pixels.length
                            && pixels[right] == MonokromConstant.PIXEL_WHITE
                            && rightDiffX == 1) { //right , chaincode = 4
                        nextDirection = 3;
                        currentPixel = right;
                        chainCodeString.append(4);
                        break;
                    }
                case 5:
                    if (bottomRight >= 0 && bottomRight < pixels.length
                            && pixels[bottomRight] == MonokromConstant.PIXEL_WHITE
                            && bottomRightDiffX == 1) { //bottom right, chaincode = 5
                        nextDirection = 3;
                        currentPixel = bottomRight;
                        chainCodeString.append(5);
                        break;
                    } else if (bottom >= 0 && bottom < pixels.length
                            && pixels[bottom] == MonokromConstant.PIXEL_WHITE) { //bottom, chaincode = 6
                        nextDirection = 5;
                        currentPixel = bottom;
                        chainCodeString.append(6);
                        break;
                    }
                case 7:
                    if (bottomLeft >= 0 && bottomLeft < pixels.length
                            && pixels[bottomLeft] == MonokromConstant.PIXEL_WHITE
                            && bottomLeftDiffX == 1) { //left bottom, chaincode = 7
                        nextDirection = 5;
                        currentPixel = bottomLeft;
                        chainCodeString.append(7);
                        break;
                    } else if (left >= 0 && left < pixels.length
                            && pixels[left] == MonokromConstant.PIXEL_WHITE
                            && leftDiffX == 1) { //left, chaincode = 8
                        nextDirection = 7;
                        currentPixel = left;
                        chainCodeString.append(8);
                        break;
                    }
                case 9:
                    if (topLeft >= 0 && topLeft < pixels.length
                            && pixels[topLeft] == MonokromConstant.PIXEL_WHITE
                            && topLeftDiffX == 1) { //top left, chaincode = 9
                        nextDirection = 7;
                        currentPixel = topLeft;
                        chainCodeString.append(9);
                        break;
                    } else if (top >= 0 && top < pixels.length && pixels[top] == MonokromConstant.PIXEL_WHITE) { //top, chaincode = 2
                        nextDirection = 9;
                        currentPixel = top;
                        chainCodeString.append(2);
                        break;
                    } else if (topRight >= 0 && topRight < pixels.length
                            && pixels[topRight] == MonokromConstant.PIXEL_WHITE
                            && topRightDiffX == 1) { //top right , chaincode = 3
                        nextDirection = 9;
                        currentPixel = topRight;
                        chainCodeString.append(3);
                        break;
                    } else if (right >= 0 && right < pixels.length
                            && pixels[right] == MonokromConstant.PIXEL_WHITE
                            && rightDiffX == 1) { //right , chaincode = 4
                        nextDirection = 3;
                        currentPixel = right;
                        chainCodeString.append(4);
                        break;
                    } else if (bottomRight >= 0 && bottomRight < pixels.length
                            && pixels[bottomRight] == MonokromConstant.PIXEL_WHITE
                            && bottomRightDiffX == 1) { //bottom right, chaincode = 5
                        nextDirection = 3;
                        currentPixel = bottomRight;
                        chainCodeString.append(5);
                        break;
                    } else if (bottom >= 0 && bottom < pixels.length && pixels[bottom] == MonokromConstant.PIXEL_WHITE) { //bottom, chaincode = 6
                        nextDirection = 5;
                        currentPixel = bottom;
                        chainCodeString.append(6);
                        break;
                    } else if (bottomLeft >= 0 && bottomLeft < pixels.length
                            && pixels[bottomLeft] == MonokromConstant.PIXEL_WHITE
                            && bottomLeftDiffX == 1) { //left bottom, chaincode = 7
                        nextDirection = 5;
                        currentPixel = bottomLeft;
                        chainCodeString.append(7);
                        break;
                    } else if (left >= 0 && left < pixels.length
                            && pixels[left] == MonokromConstant.PIXEL_WHITE
                            && leftDiffX == 1) { //left, chaincode = 8
                        nextDirection = 7;
                        currentPixel = left;
                        chainCodeString.append(8);
                        break;
                    }

            }
        }

        return chainCodeString.toString();
    }
}
