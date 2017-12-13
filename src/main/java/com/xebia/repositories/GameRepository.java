package com.xebia.repositories;

import com.xebia.domains.Game;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public interface GameRepository extends CrudRepository<Game, Integer> {

    Game findByGameId(String gameId);

    //TODO: Check
    /*List<Game> findAll();*/

}
