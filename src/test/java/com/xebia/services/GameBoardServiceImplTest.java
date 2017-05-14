package com.xebia.services;

import com.xebia.services.gameboard.GameBoard;
import com.xebia.services.gameboard.GameBoardServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by artur.skrzydlo on 2017-05-13.
 */
//TODO: some more tests ?
@RunWith(SpringRunner.class)
public class GameBoardServiceImplTest {

    @Mock
    private GameBoard gameBoard;

    @Mock
    private GameBoardRepoService gameBoardRepoService;

    @InjectMocks
    private GameBoardServiceImpl gameBoardService;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void nonEmptyGameBoard() {

        GameBoard gameBoard = gameBoardService.createGameBoard();
        Assert.assertNotNull(gameBoard);
        Assert.assertEquals(gameBoard.getFieldsCollection().size(), GameBoard.BOARD_SIZE * GameBoard.BOARD_SIZE);
    }


}
