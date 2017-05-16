package com.xebia.services.game;

import com.xebia.dto.*;
import com.xebia.exceptions.GameHasFinishedException;
import com.xebia.exceptions.NoSuchGameException;

/**
 * Created by artur.skrzydlo on 2017-05-14.
 */

public interface GameServiceClient {

    SalvoResultDTO fireSalvo(Integer gameId, SalvoDTO salvo) throws NoSuchGameException, GameHasFinishedException;

    GameStatusDTO getGameStatus(Integer gameId) throws NoSuchGameException;

    GameCreatedDTO challengePlayerForAGame(PlayerDTO playerDTO);

    void turnOnAutopilot(Integer gameId);
}
