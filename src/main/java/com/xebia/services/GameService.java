package com.xebia.services;

import com.xebia.dto.GameCreatedDTO;
import com.xebia.dto.PlayerDTO;
import com.xebia.dto.SalvoDTO;
import com.xebia.dto.SalvoResultDTO;
import com.xebia.exceptions.NoSuchGameException;
import com.xebia.exceptions.ShotOutOfBoardException;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public interface GameService {

    GameCreatedDTO createNewGame(PlayerDTO player);

    SalvoResultDTO receiveSalvo(SalvoDTO salvoDTO, Integer gameId) throws ShotOutOfBoardException, NoSuchGameException;

}
