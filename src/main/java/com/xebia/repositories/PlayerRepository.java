package com.xebia.repositories;

import com.xebia.domains.Player;
import com.xebia.enums.PlayerType;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public interface PlayerRepository extends CrudRepository<Player, Integer> {

    Player findByPlayerType(PlayerType playerType);

    Player findByUserIdAndFullName(String userId, String fullName);
}
