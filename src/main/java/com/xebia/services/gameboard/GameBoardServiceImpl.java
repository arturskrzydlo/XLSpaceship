package com.xebia.services.gameboard;

import com.xebia.services.GameBoardRepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by artur.skrzydlo on 2017-05-12.
 */
@Component
public class GameBoardServiceImpl implements GameBoardService {


    @Autowired
    private GameBoardRepoService gameBoardRepoService;

    @Override
    public GameBoard createGameBoard() {
        GameBoard gameBoard = new GameBoard();
        gameBoard.placeSpaceshipsOnTheBoard();

        gameBoardRepoService.batchSave(gameBoard.getFields());
        return gameBoard;
    }
}
