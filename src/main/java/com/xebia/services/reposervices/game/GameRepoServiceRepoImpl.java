package com.xebia.services.reposervices.game;

import com.xebia.domains.Game;
import com.xebia.repositories.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
@Service
public class GameRepoServiceRepoImpl implements GameRepoService {

    private GameRepository gameRepository;

    @Autowired
    public void setGameRepository(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public List<Game> listAll() {
        return StreamSupport.stream(gameRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Game getByGameId(Integer id) {
        return gameRepository.findOne(id);
    }

    @Override
    public Game saveOrUpdate(Game domainObject) {
        return gameRepository.save(domainObject);
    }

    @Override
    public Game getByGameId(String gameId) {
        return gameRepository.findByGameId(gameId);
    }

    @Override
    public void delete(Integer id) {
        gameRepository.delete(id);
    }
}
