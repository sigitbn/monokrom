package com.bimosigit.monokrom.model;

import com.google.firebase.database.Exclude;

import java.util.List;

/**
 * Created by sigitbn on 10/20/17.
 */

public class Component {

    @Exclude
    int[] componentPixels;

    String chainCode;
    int centroid;
    int maxX;
    int minX;

    int maxY;
    int minY;

    int height;
    int length;



    List<Integer> chainCodeCoordinate;

    public Component(int[] componentPixels, String chainCode, int centroid) {
        this.componentPixels = componentPixels;
        this.chainCode = chainCode;
        this.centroid = centroid;
    }

    public Component() {
    }
    @Exclude
    public int[] getComponentPixels() {
        return componentPixels;
    }

    public String getChainCode() {
        return chainCode;
    }

    public int getCentroid() {
        return centroid;
    }

    public List<Integer> getChainCodeCoordinate() {
        return chainCodeCoordinate;
    }
    @Exclude
    public void setComponentPixels(int[] componentPixels) {
        this.componentPixels = componentPixels;
    }

    public void setChainCode(String chainCode) {
        this.chainCode = chainCode;
    }

    public void setCentroid(int centroid) {
        this.centroid = centroid;
    }

    public void setChainCodeCoordinate(List<Integer> chainCodeCoordinate) {
        this.chainCodeCoordinate = chainCodeCoordinate;
    }

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMinX() {
        return minX;
    }

    public void setMinX(int minX) {
        this.minX = minX;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public int getMinY() {
        return minY;
    }

    public void setMinY(int minY) {
        this.minY = minY;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
