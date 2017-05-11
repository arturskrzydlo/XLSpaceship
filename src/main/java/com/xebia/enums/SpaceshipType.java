package com.xebia.enums;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public enum SpaceshipType {

    WINGER(9), ANGLE(6), ACLASS(8), BCLASS(10), SCLASS(8);

    private int numberOfFields;

    SpaceshipType(int numberOfFields) {
        this.numberOfFields = numberOfFields;
    }
}
