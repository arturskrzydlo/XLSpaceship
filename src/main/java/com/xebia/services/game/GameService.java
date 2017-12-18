package com.xebia.services.game;

import com.xebia.dto.GameCreatedDTO;
import com.xebia.dto.PlayerDTO;
import com.xebia.dto.SalvoDTO;
import com.xebia.dto.SalvoResultDTO;
import com.xebia.exceptions.NoSuchGameException;
import com.xebia.exceptions.NotYourTurnException;
import com.xebia.exceptions.ShotOutOfBoardException;

import java.util.concurrent.ExecutionException;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public interface GameService {

    GameCreatedDTO createNewGame(PlayerDTO player) throws NotYourTurnException;

    SalvoResultDTO receiveSalvo(SalvoDTO salvoDTO, String gameId) throws ShotOutOfBoardException, NoSuchGameException, NotYourTurnException, ExecutionException, InterruptedException;

    void updateGameAfterYourSalvo(SalvoResultDTO salvoResultDTO, String gameId);
}
