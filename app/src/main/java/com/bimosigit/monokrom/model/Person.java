package com.bimosigit.monokrom.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by sigitbn on 10/24/17.
 */

@IgnoreExtraProperties
public class Person {

    @Exclude
    byte[] bytes;

    int width;
    int height;
    double goldenRatio;
    String name;

    @Exclude
    List<Component> components;

    public int getDistance() {
        return distance;
    }

    private int distance;

    public Person() {
    }

    public Person(long goldenRatio, String name) {
        this.goldenRatio = goldenRatio;
        this.name = name;
    }

    @Exclude
    public byte[] getBytes() {
        return bytes;
    }

    public double getGoldenRatio() {
        return goldenRatio;
    }

    public String getName() {
        return name;
    }

    @Exclude
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public void setGoldenRatio(double goldenRatio) {
        this.goldenRatio = goldenRatio;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public List<Component> getComponents() {
        return components;
    }
    @Exclude
    public void setComponents(List<Component> components) {
        this.components = components;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
