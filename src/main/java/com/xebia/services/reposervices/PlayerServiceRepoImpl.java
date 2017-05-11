package com.xebia.services.reposervices;

import com.xebia.domains.Player;
import com.xebia.repositories.PlayerRepository;
import com.xebia.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public class PlayerServiceRepoImpl implements PlayerService {

    private PlayerRepository playerRepository;

    @Autowired
    public void setPlayerRepository(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public List<Player> listAll() {
        return StreamSupport.stream(playerRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Player getById(Integer id) {
        return playerRepository.findOne(id);
    }

    @Override
    public Player saveOrUpdate(Player domainObject) {
        return playerRepository.save(domainObject);
    }

    @Override
    public void delete(Integer id) {
        playerRepository.delete(id);
    }
}
