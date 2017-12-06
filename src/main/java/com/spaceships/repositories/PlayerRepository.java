package com.spaceships.repositories;

import com.spaceships.domains.Player;
import com.spaceships.enums.PlayerType;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public interface PlayerRepository extends CrudRepository<Player, Integer> {

    Player findByPlayerType(PlayerType playerType);

    Player findByUserIdAndFullName(String userId, String fullName);
}
