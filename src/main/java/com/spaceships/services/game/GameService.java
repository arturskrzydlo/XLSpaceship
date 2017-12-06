package com.spaceships.services.game;

import com.spaceships.dto.GameCreatedDTO;
import com.spaceships.dto.PlayerDTO;
import com.spaceships.dto.SalvoDTO;
import com.spaceships.dto.SalvoResultDTO;
import com.spaceships.exceptions.NoSuchGameException;
import com.spaceships.exceptions.NotYourTurnException;
import com.spaceships.exceptions.ShotOutOfBoardException;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public interface GameService {

    GameCreatedDTO createNewGame(PlayerDTO player) throws NotYourTurnException;

    SalvoResultDTO receiveSalvo(SalvoDTO salvoDTO, String gameId) throws ShotOutOfBoardException, NoSuchGameException, NotYourTurnException;

    void updateGameAfterYourSalvo(SalvoResultDTO salvoResultDTO, String gameId);
}
