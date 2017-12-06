package com.spaceships.services.reposervices.gameboard;

import com.spaceships.domains.GameBoardPosition;
import com.spaceships.services.reposervices.CRUDService;

import java.util.List;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public interface GameBoardRepoService extends CRUDService<GameBoardPosition> {

    List<GameBoardPosition> batchSave(List<GameBoardPosition> positions);

    List<GameBoardPosition> getOwnerGameBoardByGame(String gameId);

    List<GameBoardPosition> getOpponentPlayerByGame(String gameId, Integer playerId);

}
