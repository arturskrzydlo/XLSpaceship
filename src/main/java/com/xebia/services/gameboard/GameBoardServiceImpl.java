package com.xebia.services.gameboard;

import com.xebia.services.GameBoardRepoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by artur.skrzydlo on 2017-05-12.
 */
@Component
public class GameBoardServiceImpl implements GameBoardService {

    private Logger logger = LoggerFactory.getLogger(GameBoardServiceImpl.class);

    @Autowired
    private GameBoardRepoService gameBoardRepoService;

    @Override
    public GameBoard createGameBoard() {

        GameBoard gameBoard = new GameBoard();
        gameBoard.placeSpaceshipsOnTheBoard();
        gameBoardRepoService.batchSave(gameBoard.getFieldsCollection());

        logger.info(gameBoard.toString());
        return gameBoard;

    }
}
