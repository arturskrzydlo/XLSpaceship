package com.xebia;

import com.xebia.domains.GameBoardPosition;
import com.xebia.enums.SpaceshipType;
import com.xebia.services.gameboard.GameBoard;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Created by artur.skrzydlo on 2017-05-13.
 */


@RunWith(MockitoJUnitRunner.class)
public class GameBoardTest {

    private GameBoard gameBoard;

    @Before
    public void prepareTests() {
        gameBoard = new GameBoard();
    }

    @Test
    public void testSizeOfEmptyGameBoard() {

        GameBoardPosition[][] fields = gameBoard.getFields();
        assertEquals(fields.length, GameBoard.BOARD_SIZE);
        assertEquals(fields[0].length, GameBoard.BOARD_SIZE);
    }

    @Test
    public void testEmptyGameBoardFieldsAreInitialized() {

        GameBoardPosition[][] fields = gameBoard.getFields();
        Arrays.stream(fields).flatMap(Arrays::stream).forEach(gameBoardPosition -> assertNotNull(gameBoardPosition));

    }

    @Test
    public void testEmptyGameBoardFieldsHasCorrectColumnAndRow() {
        GameBoardPosition[][] fields = gameBoard.getFields();
        Arrays.stream(fields).flatMap(Arrays::stream).forEach(gameBoardPosition -> {

            int column = Integer.parseInt(gameBoardPosition.getColumn().toString(), 16);
            int row = Integer.parseInt(gameBoardPosition.getRow().toString(), 16);

            GameBoardPosition fromFields = new GameBoardPosition();
            fromFields = fields[row][column];
            assertEquals(gameBoardPosition, fromFields);
        });
    }

    @Test
    public void testAllTypesOneOfEachAreInitializedAfterSpaceshipPlacement() {
        gameBoard.placeSpaceshipsOnTheBoard();
        assertEquals(gameBoard.getSpaceships().size(), SpaceshipType.values().length);
    }

    @Test
    public void testAllSpaceshipsHasBeenPlaced() {
        gameBoard.placeSpaceshipsOnTheBoard();
        Set<SpaceshipType> spaceshipTypes = Arrays.stream(gameBoard.getFields()).
                flatMap(Arrays::stream).
                filter(gameBoardPosition -> gameBoardPosition.getSpaceship() != null).
                map(gameBoardPosition -> gameBoardPosition.getSpaceship().getType()).
                collect(Collectors.toSet());

        assertEquals(spaceshipTypes.size(), SpaceshipType.values().length);
    }

    @Test
    public void testAllSpaceshipsHasProperNumberOfElements() {
        gameBoard.placeSpaceshipsOnTheBoard();
        Map<SpaceshipType, Long> spaceshipTypeLongMap = gameBoard.getFieldsCollection().stream().
                filter(gameBoardPosition -> gameBoardPosition.getSpaceship() != null).
                collect(Collectors.groupingBy(gameBoardPosition -> gameBoardPosition.getSpaceship().getType(), Collectors.counting()));

        boolean result = Arrays.stream(SpaceshipType.values()).
                allMatch(spaceshipType -> spaceshipTypeLongMap.get(spaceshipType).equals(new Long(spaceshipType.getNumberOfFields())));
        assertTrue(result);
    }

}
