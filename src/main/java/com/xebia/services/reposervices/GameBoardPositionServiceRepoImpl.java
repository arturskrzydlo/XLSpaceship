package com.xebia.services.reposervices;

import com.xebia.domains.GameBoardPosition;
import com.xebia.repositories.GameBoardPositionRepository;
import com.xebia.services.GameBoardService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public class GameBoardPositionServiceRepoImpl implements GameBoardService {

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
}
