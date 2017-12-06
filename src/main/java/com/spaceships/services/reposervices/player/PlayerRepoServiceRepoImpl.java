package com.spaceships.services.reposervices.player;

import com.spaceships.domains.Player;
import com.spaceships.dto.PlayerDTO;
import com.spaceships.enums.PlayerType;
import com.spaceships.repositories.PlayerRepository;
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
    public Player getByGameId(Integer id) {
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


    @Override
    public Player findMyPlayer() {
        return playerRepository.findByPlayerType(PlayerType.OWNER);
    }

    @Override
    public Player findOpponentPlayer(PlayerDTO opponent) {
        return playerRepository.findByUserIdAndFullName(opponent.getUserId(), opponent.getFullName());
    }
}
