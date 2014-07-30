package com.carmen.provacampionamentoaccelerometro;

import java.io.Serializable;

/**
 * Created by carmen on 27/07/14.
 */
public class AccelerationSample implements Serializable {
    private float componentX, componentY, componentZ;
    long timestamp;

    public AccelerationSample(float componentX, float componentY, float componentZ, long timestamp) {
        this.componentX = componentX;
        this.componentY = componentY;
        this.componentZ = componentZ;
        this.timestamp = timestamp;

    }

    public float getComponentX() {
        return componentX;
    }

    public float getComponentY() {
        return componentY;
    }

    public float getComponentZ() {
        return componentZ;
    }

    @Override
    public String toString() {
        return
                "X = " + componentX +
                ", \tY = " + componentY +
                ", \tZ = " + componentZ +
                "   \t sampled  " + MainActivity.getDate(timestamp,"dd/MM/yyyy hh:mm:ss.SSS");
    }
}
