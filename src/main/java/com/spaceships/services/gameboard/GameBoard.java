package com.spaceships.services.gameboard;


import com.spaceships.domains.GameBoardPosition;
import com.spaceships.domains.Spaceship;
import com.spaceships.enums.SpaceshipType;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Created by artur.skrzydlo on 2017-05-12.
 */

public class GameBoard {

    // rotation by which spaceship can be rotated in degrees
    public static final int ROTATAION_ANGLE = 90;
    public static final int BOARD_SIZE = 16;
    public static final boolean CAN_BE_BEIGHBOUR = false;

    private GameBoardPosition[][] fields = new GameBoardPosition[BOARD_SIZE][BOARD_SIZE];
    private List<Spaceship> spaceships;

    public GameBoard() {
        initializeGameBoard();
    }

    private void initializeGameBoard() {

        fields = new GameBoardPosition[BOARD_SIZE][BOARD_SIZE];

        for (int row = 0; row < BOARD_SIZE; row++) {

            for (int column = 0; column < BOARD_SIZE; column++) {

                GameBoardPosition position = new GameBoardPosition();
                position.setRow(Integer.toHexString(row).charAt(0));
                position.setColumn(Integer.toHexString(column).charAt(0));

                fields[row][column] = position;
            }
        }
    }

    private void initializeSpaceshipFormation() {

        spaceships = new ArrayList<>();
        Arrays.stream(SpaceshipType.values()).forEach(spaceshipType -> spaceships.add(new Spaceship(spaceshipType)));

    }

    public void placeSpaceshipsOnTheBoard() {

        initializeSpaceshipFormation();
        if (!spaceships.stream().allMatch(this::placeSingleSpaceShipOnTheBoard)) {
            resetPlacing();
        }

    }

    //Method just in case that random placement cannot be achieved in that spaceship configuration
    // it will reset and try again. Assumption is that board can contain all of the spaceships
    private void resetPlacing() {
        initializeGameBoard();
        placeSpaceshipsOnTheBoard();
    }

    private boolean placeSingleSpaceShipOnTheBoard(Spaceship spaceship) {

        Set<Point2D> spaceShipConstruction;
        List<GameBoardPosition> notCheckedPositions = Arrays.stream(fields).flatMap(Arrays::stream).collect(Collectors.toList());
        List<Integer> notCheckedRotations = new ArrayList<>();


        boolean positionFound = false;

        while (!positionFound) {

            Optional<GameBoardPosition> choosenPosition = chooseRandomlyFromList(notCheckedPositions);
            notCheckedRotations.clear();
            IntStream.range(1, 5).forEach(rotation -> notCheckedRotations.add(rotation));


            if (!choosenPosition.isPresent()) {
                return false;
            }

            while (!notCheckedRotations.isEmpty() && !positionFound) {
                spaceShipConstruction = spaceship.getType().getSpaceshipConstruction();

                int rotation = chooseRandomlyFromList(notCheckedRotations).get();

                //only B can be mirrored. If rotation would be  2, then it will be 180 degrees and it will be mirrored
                if (rotation == 2 && spaceship.getType().equals(SpaceshipType.BCLASS)) {
                    rotation = 3;
                }

                rotateSpaceshipConstruction(rotation, spaceShipConstruction);
                transformConstrutionToChoosenPosition(spaceShipConstruction, choosenPosition.get());
                positionFound = placeSpaceshipConstruction(spaceship, spaceShipConstruction);

            }
        }

        return true;
    }

    private boolean placeSpaceshipConstruction(Spaceship spaceship, Set<Point2D> spaceshipConstruction) {

        boolean result = checkIfCanPlaceSpaceShipOnCoordinates(spaceshipConstruction, spaceship.getType());

        if (result) {
            spaceshipConstruction.stream().forEach(point -> {
                int column = Double.valueOf(point.getX()).intValue();
                int row = Double.valueOf(point.getY()).intValue();
                GameBoardPosition gameBoardPosition = fields[column][row];
                gameBoardPosition.setSpaceship(spaceship);
            });
        }

        return result;
    }

    private boolean checkIfCanPlaceSpaceShipOnCoordinates(Set<Point2D> spaceshipConstruction, SpaceshipType spaceshipType) {
        return spaceshipConstruction.stream().allMatch(point -> {

            int column = Double.valueOf(point.getX()).intValue();
            int row = Double.valueOf(point.getY()).intValue();

            if (row >= fields.length || column >= fields[0].length || row < 0 || column < 0) {
                return false;
            }

            GameBoardPosition gameBoardPosition = fields[column][row];

            if (gameBoardPosition.getSpaceship() != null) {
                return false;
            }

            if (!CAN_BE_BEIGHBOUR && checkIfPositionIsNeighbourToOtherShip(column, row, spaceshipType)) {
                return false;
            }

            return true;
        });
    }

    private boolean checkIfPositionIsNeighbourToOtherShip(int column, int row, SpaceshipType spaceshipType) {

        for (int rowIndex = -1; rowIndex <= 1; rowIndex++) {
            for (int columnIndex = -1; columnIndex <= 1; columnIndex++) {
                if (rowIndex == 0 && columnIndex == 0) {
                    continue;
                }

                int newColumn = column + columnIndex;
                int newRow = row + rowIndex;

                if (newColumn < 0 || newColumn >= BOARD_SIZE || newRow < 0 || newRow >= BOARD_SIZE) {
                    continue;
                }

                Spaceship spaceshipOnPosition = fields[newColumn][newRow].getSpaceship();
                if (spaceshipOnPosition != null && !spaceshipOnPosition.getType().equals(spaceshipType)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void transformConstrutionToChoosenPosition(Set<Point2D> spaceshipConstruction, GameBoardPosition choosenPosition) {

        double columnTransformation = Integer.parseInt(choosenPosition.getColumn().toString(), 16);
        double rowTransformation = Integer.parseInt(choosenPosition.getRow().toString(), 16);

        spaceshipConstruction.stream().forEach(point -> {
            point.setLocation(point.getX() + columnTransformation, point.getY() + rowTransformation);

        });

        spaceshipConstruction.add(new Point2D.Double(columnTransformation, rowTransformation));
    }

    private <T> Optional<T> chooseRandomlyFromList(List<T> listToChooseFrom) {

        if (listToChooseFrom.size() > 0) {

            Optional<T> result;
            int index = ThreadLocalRandom.current().nextInt(0, listToChooseFrom.size());
            result = Optional.of(listToChooseFrom.get(index));
            listToChooseFrom.remove(index);
            return result;

        } else {
            return Optional.empty();
        }
    }


    private void rotateSpaceshipConstruction(int angleLevel, Set<Point2D> spaceShipConstruction) {

        spaceShipConstruction.stream().forEach(point -> {

            Point2D result = new Point2D.Double();
            Point2D pivot = new Point2D.Double(0, 0);

            AffineTransform rotation = new AffineTransform();

            rotation.rotate(Math.toRadians(ROTATAION_ANGLE * angleLevel), pivot.getX(), pivot.getY());
            rotation.transform(point, result);

            point.setLocation(result);

        });
    }

    public List<GameBoardPosition> getFieldsCollection() {
        return Arrays.stream(fields).flatMap(Arrays::stream).collect(Collectors.toList());
    }

    public GameBoardPosition[][] getFields() {
        return fields;
    }

    public List<Spaceship> getSpaceships() {
        return spaceships;
    }

    @Override
    public String toString() {

        StringBuilder stringBuilder = new StringBuilder("\n");


        for (int row = 0; row < fields.length; row++) {
            int column = 0;

            //print columns (Y) axis values
            if (row == 0) {
                stringBuilder.append("   ");
                for (column = 0; column < BOARD_SIZE; column++) {
                    stringBuilder.append(fields[row][column].getColumn() + " ");
                }
                stringBuilder.append("\n");
                column = 0;
            }

            //print rows (X) axis values
            stringBuilder.append(fields[row][column].getRow() + "  ");


            for (column = 0; column < fields[0].length; column++) {

                GameBoardPosition position = fields[row][column];

                if (position.getSpaceship() == null) {
                    stringBuilder.append("- ");
                } else {
                    stringBuilder.append("X ");
                }
            }

            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}
