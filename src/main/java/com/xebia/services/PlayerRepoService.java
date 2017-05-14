package com.xebia.services;

import com.xebia.domains.Player;
import com.xebia.dto.PlayerDTO;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public interface PlayerRepoService extends CRUDService<Player> {

    Player findMyPlayer();

    Player findOpponentPlayer(PlayerDTO opponent);

}
