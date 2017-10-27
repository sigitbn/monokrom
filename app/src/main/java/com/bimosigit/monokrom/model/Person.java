package com.bimosigit.monokrom.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by sigitbn on 10/24/17.
 */

@IgnoreExtraProperties
public class Person {

    @Exclude
    byte[] bytes;

    double goldenRatio;
    String name;

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
}
