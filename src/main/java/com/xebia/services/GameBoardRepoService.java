package com.xebia.services;

import com.xebia.domains.GameBoardPosition;

import java.util.List;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public interface GameBoardRepoService extends CRUDService<GameBoardPosition> {

    List<GameBoardPosition> batchSave(List<GameBoardPosition> positions);

    List<GameBoardPosition> getOwnerGameBoardByGame(Integer gameId);

    List<GameBoardPosition> getOpponentPlayerByGame(Integer gameId, Integer playerId);

}
