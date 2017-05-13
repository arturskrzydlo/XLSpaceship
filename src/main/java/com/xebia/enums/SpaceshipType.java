package com.xebia.enums;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public enum SpaceshipType {

    WINGER(9) {
        @Override
        public Set<Point2D> getSpaceshipConstruction() {

            //   * *
            //   * *
            //    *
            //   * *
            //   * *

            Set<Point2D> spaceshipConstruction = new HashSet<>();

            spaceshipConstruction.add(new Point2D.Double(2, -1));
            spaceshipConstruction.add(new Point2D.Double(2, 1));
            spaceshipConstruction.add(new Point2D.Double(1, -1));
            spaceshipConstruction.add(new Point2D.Double(1, 1));
            spaceshipConstruction.add(new Point2D.Double(-1, -1));
            spaceshipConstruction.add(new Point2D.Double(-1, 1));
            spaceshipConstruction.add(new Point2D.Double(-2, -1));
            spaceshipConstruction.add(new Point2D.Double(-2, 1));

            return spaceshipConstruction;
        }
    }, ANGLE(6) {
        @Override
        public Set<Point2D> getSpaceshipConstruction() {

            Set<Point2D> spaceshipConstruction = new HashSet<>();

            spaceshipConstruction.add(new Point2D.Double(-3, 0));
            spaceshipConstruction.add(new Point2D.Double(-2, 0));
            spaceshipConstruction.add(new Point2D.Double(-1, 0));
            spaceshipConstruction.add(new Point2D.Double(0, 1));
            spaceshipConstruction.add(new Point2D.Double(0, 2));


            return spaceshipConstruction;
        }
    }, ACLASS(8) {
        @Override
        public Set<Point2D> getSpaceshipConstruction() {
            Set<Point2D> spaceshipConstruction = new HashSet<>();

            spaceshipConstruction.add(new Point2D.Double(2, 0));
            spaceshipConstruction.add(new Point2D.Double(1, -1));
            spaceshipConstruction.add(new Point2D.Double(1, 1));
            spaceshipConstruction.add(new Point2D.Double(0, -1));
            spaceshipConstruction.add(new Point2D.Double(0, 1));
            spaceshipConstruction.add(new Point2D.Double(-1, -1));
            spaceshipConstruction.add(new Point2D.Double(-1, 1));

            return spaceshipConstruction;
        }
    }, BCLASS(10) {
        @Override
        public Set<Point2D> getSpaceshipConstruction() {
            Set<Point2D> spaceshipConstruction = new HashSet<>();

            spaceshipConstruction.add(new Point2D.Double(2, 1));
            spaceshipConstruction.add(new Point2D.Double(1, 2));
            spaceshipConstruction.add(new Point2D.Double(0, 1));
            spaceshipConstruction.add(new Point2D.Double(-1, 2));
            spaceshipConstruction.add(new Point2D.Double(-2, 1));
            spaceshipConstruction.add(new Point2D.Double(-2, 0));
            spaceshipConstruction.add(new Point2D.Double(-1, 0));
            spaceshipConstruction.add(new Point2D.Double(1, 0));
            spaceshipConstruction.add(new Point2D.Double(2, 0));

            return spaceshipConstruction;
        }
    }, SCLASS(8) {
        @Override
        public Set<Point2D> getSpaceshipConstruction() {
            Set<Point2D> spaceshipConstruction = new HashSet<>();

            spaceshipConstruction.add(new Point2D.Double(2, 0));
            spaceshipConstruction.add(new Point2D.Double(2, 1));
            spaceshipConstruction.add(new Point2D.Double(1, -1));
            spaceshipConstruction.add(new Point2D.Double(0, 1));
            spaceshipConstruction.add(new Point2D.Double(-1, 2));
            spaceshipConstruction.add(new Point2D.Double(-2, 1));
            spaceshipConstruction.add(new Point2D.Double(-2, 0));

            return spaceshipConstruction;
        }
    };

    private int numberOfFields;

    SpaceshipType(int numberOfFields) {
        this.numberOfFields = numberOfFields;
    }


    public abstract Set<Point2D> getSpaceshipConstruction();

}
