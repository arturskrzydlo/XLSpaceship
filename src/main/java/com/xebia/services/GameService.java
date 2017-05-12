package com.xebia.services;

import com.xebia.dto.GameCreatedDTO;
import com.xebia.dto.PlayerDTO;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public interface GameService {

    GameCreatedDTO createNewGame(PlayerDTO player);

}
