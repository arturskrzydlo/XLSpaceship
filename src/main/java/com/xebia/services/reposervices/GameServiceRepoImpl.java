package com.xebia.services.reposervices;

import com.xebia.domains.Game;
import com.xebia.repositories.GameRepository;
import com.xebia.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
@Service
public class GameServiceRepoImpl implements GameService {

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
    public Game getById(Integer id) {
        return gameRepository.findOne(id);
    }

    @Override
    public Game saveOrUpdate(Game domainObject) {
        return gameRepository.save(domainObject);
    }

    @Override
    public void delete(Integer id) {
        gameRepository.delete(id);
    }
}
