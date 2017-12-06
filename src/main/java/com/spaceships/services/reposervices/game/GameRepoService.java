package com.spaceships.services.reposervices.game;

import com.spaceships.domains.Game;
import com.spaceships.services.reposervices.CRUDService;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public interface GameRepoService extends CRUDService<Game> {

    Game getByGameId(String gameId);

}
