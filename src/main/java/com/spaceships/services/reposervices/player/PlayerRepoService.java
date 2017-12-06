package com.spaceships.services.reposervices.player;

import com.spaceships.domains.Player;
import com.spaceships.dto.PlayerDTO;
import com.spaceships.services.reposervices.CRUDService;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public interface PlayerRepoService extends CRUDService<Player> {

    Player findMyPlayer();

    Player findOpponentPlayer(PlayerDTO opponent);

}
