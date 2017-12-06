package com.spaceships.repositories;

import com.spaceships.domains.GameBoardPosition;
import com.spaceships.enums.PlayerType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public interface GameBoardPositionRepository extends CrudRepository<GameBoardPosition, Integer> {

    List<GameBoardPosition> findByGameGameIdAndPlayerPlayerType(String gameId, PlayerType playerType);

    List<GameBoardPosition> findByGameGameIdAndPlayerId(String gameId, Integer playerId);

}
