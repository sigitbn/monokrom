package com.bimosigit.monokrom.model;

/**
 * Created by sigitbn on 10/20/17.
 */

public class Component {

    int[] componentPixels;
    String chainCode;
    int centroid;

    public Component(int[] componentPixels, String chainCode, int centroid) {
        this.componentPixels = componentPixels;
        this.chainCode = chainCode;
        this.centroid = centroid;
    }

    public int[] getComponentPixels() {
        return componentPixels;
    }

    public String getChainCode() {
        return chainCode;
    }

    public int getCentroid() {
        return centroid;
    }
}
