package com.spaceships.services.game;

import com.spaceships.dto.*;
import com.spaceships.exceptions.GameHasFinishedException;
import com.spaceships.exceptions.NoSuchGameException;

import java.util.List;

/**
 * Created by artur.skrzydlo on 2017-05-14.
 */

public interface GameServiceClient {

    SalvoResultDTO fireSalvo(String gameId, SalvoDTO salvo) throws NoSuchGameException, GameHasFinishedException;

    GameStatusDTO getGameStatus(String gameId) throws NoSuchGameException;

    GameCreatedDTO challengePlayerForAGame(PlayerDTO playerDTO);

    void turnOnAutopilot(String gameId);

    List<GameDTO> getAllGames();

    PlayerDTO getOwnerPlayerData();
}
