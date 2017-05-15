package com.xebia.services.reposervices.gameboard;

import com.xebia.domains.GameBoardPosition;
import com.xebia.enums.PlayerType;
import com.xebia.repositories.GameBoardPositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
@Service
public class GameBoardPositionRepoServiceRepoImpl implements GameBoardRepoService {

    private GameBoardPositionRepository gameBoardPositionRepository;

    @Autowired
    public void setGameBoardPositionRepository(GameBoardPositionRepository gameBoardPositionRepository) {
        this.gameBoardPositionRepository = gameBoardPositionRepository;
    }

    @Override
    public List<GameBoardPosition> listAll() {
        return StreamSupport.stream(gameBoardPositionRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public GameBoardPosition getById(Integer id) {
        return gameBoardPositionRepository.findOne(id);
    }

    @Override
    public GameBoardPosition saveOrUpdate(GameBoardPosition domainObject) {
        return gameBoardPositionRepository.save(domainObject);
    }

    @Override
    public void delete(Integer id) {
        gameBoardPositionRepository.delete(id);
    }


    @Override
    public List<GameBoardPosition> batchSave(List<GameBoardPosition> positions) {
        return (List<GameBoardPosition>) gameBoardPositionRepository.save(positions);
    }

    @Override
    public List<GameBoardPosition> getOwnerGameBoardByGame(Integer gameId) {
        return gameBoardPositionRepository.findByGameIdAndPlayerPlayerType(gameId, PlayerType.OWNER);
    }

    @Override
    public List<GameBoardPosition> getOpponentPlayerByGame(Integer gameId, Integer playerId) {
        return gameBoardPositionRepository.findByGameIdAndPlayerId(gameId, playerId);
    }

}
