package com.xebia.services.game;

import com.xebia.dto.SalvoDTO;
import com.xebia.dto.SalvoResultDTO;

/**
 * Created by artur.skrzydlo on 2017-05-14.
 */

public interface GameServiceClient {

    SalvoResultDTO fireSalvo(Integer gameId, SalvoDTO salvo);
}
