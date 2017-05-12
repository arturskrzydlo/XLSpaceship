package com.xebia.services.implementations.reposervices;

import com.xebia.domains.Player;
import com.xebia.repositories.PlayerRepository;
import com.xebia.services.PlayerRepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
@Service
public class PlayerRepoServiceRepoImpl implements PlayerRepoService {

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
