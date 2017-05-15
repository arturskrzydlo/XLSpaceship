package com.xebia.services.reposervices.player;

import com.xebia.domains.Player;
import com.xebia.dto.PlayerDTO;
import com.xebia.services.reposervices.CRUDService;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public interface PlayerRepoService extends CRUDService<Player> {

    Player findMyPlayer();

    Player findOpponentPlayer(PlayerDTO opponent);

}
