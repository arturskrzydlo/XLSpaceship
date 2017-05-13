package com.xebia.services.gameboard;

import com.xebia.domains.GameBoardPosition;
import com.xebia.domains.Spaceship;
import com.xebia.enums.SpaceshipType;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by artur.skrzydlo on 2017-05-12.
 */
//TODO: Algorithm for check if spaceship is mirrored

public class GameBoard {

    // rotation by which spaceship can be rotated in degrees
    public static final int ROTATAION_ANGLE = 90;
    public static final int BOARD_SIZE = 16;

    private List<GameBoardPosition> gameBoardFields;
    private GameBoardPosition[][] fields = new GameBoardPosition[BOARD_SIZE][BOARD_SIZE];
    private List<Spaceship> spaceships;

    public GameBoard() {
        initializeGameBoard();
    }

    private void initializeGameBoard() {

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
        spaceships.add(new Spaceship(SpaceshipType.WINGER));
        spaceships.add(new Spaceship(SpaceshipType.ANGLE));
        spaceships.add(new Spaceship(SpaceshipType.ACLASS));
        spaceships.add(new Spaceship(SpaceshipType.BCLASS));
        spaceships.add(new Spaceship(SpaceshipType.SCLASS));
    }

    public void placeSpaceshipsOnTheBoard() {

        initializeSpaceshipFormation();
        if (!spaceships.stream().allMatch(this::placeSingleSpaceShipOnTheBoard)) {
            resetPlacing();
        }

    }

    //Method just in case that random placement cannot be achieved in that spaceship configuration
    // it will reset and try again. Assumption is that board can contain all of the spaceships
    //TODO: limit number of tries - remove the assumption
    private void resetPlacing() {
        initializeGameBoard();
        placeSpaceshipsOnTheBoard();
    }

    private boolean placeSingleSpaceShipOnTheBoard(Spaceship spaceship) {

        Set<Point2D> spaceShipConstruction = spaceship.getType().getSpaceshipConstruction();
        List<GameBoardPosition> notCheckedPositions = Arrays.stream(fields).flatMap(Arrays::stream).collect(Collectors.toList());
        List<Integer> notCheckedRotations = new ArrayList<>();
        IntStream.range(1, 9).forEach(rotation -> notCheckedRotations.add(rotation));

        boolean positionFound = false;

        while (!positionFound) {

            Optional<GameBoardPosition> choosenPosition = chooseRandomlyFromList(notCheckedPositions);

            if (!choosenPosition.isPresent()) {
                return false;
            }

            while (!notCheckedRotations.isEmpty() && !positionFound) {
                int rotation = chooseRandomlyFromList(notCheckedRotations).get();
                rotateSpaceshipConstruction(rotation, spaceShipConstruction);
                transformConstrutionToChoosenPosition(spaceShipConstruction, choosenPosition.get());
                positionFound = placeSpaceshipConstruction(spaceship, spaceShipConstruction);

            }
        }

        return true;
    }

    private boolean placeSpaceshipConstruction(Spaceship spaceship, Set<Point2D> spaceshipConstruction) {

        boolean result = checkIfCanPlaceSpaceShipOnCoordinates(spaceshipConstruction);

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

    private boolean checkIfCanPlaceSpaceShipOnCoordinates(Set<Point2D> spaceshipConstruction) {
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
            return true;
        });
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

    public List<GameBoardPosition> getFields() {
        return Arrays.stream(fields).flatMap(Arrays::stream).collect(Collectors.toList());
    }
}
