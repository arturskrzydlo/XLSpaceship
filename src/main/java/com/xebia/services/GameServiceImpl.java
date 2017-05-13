package com.xebia.services;

import com.xebia.domains.Game;
import com.xebia.domains.Player;
import com.xebia.domains.SpaceshipProtocol;
import com.xebia.dto.GameCreatedDTO;
import com.xebia.dto.PlayerDTO;
import com.xebia.enums.GameStatus;
import com.xebia.services.gameboard.GameBoardService;
import com.xebia.util.DTOMapperUtil;
import com.xebia.util.OwnerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameRepoService gameRepoService;

    @Autowired
    private PlayerRepoService playerRepoService;

    @Autowired
    private GameBoardService gameBoardService;

    @Override
    public GameCreatedDTO createNewGame(PlayerDTO player) {

        Game newGame = new Game();

        newGame.setOwnerPlayer(createUserPlayer());

        Player opponentPlayer = DTOMapperUtil.mapPlayerDTOToPlayer(player);
        newGame.setOpponentPlayer(opponentPlayer);

        chooseRandomlyStartingPlayer(newGame);
        newGame.setStatus(GameStatus.ACTIVE);

        newGame = gameRepoService.saveOrUpdate(newGame);

        gameBoardService.createGameBoard();

        return DTOMapperUtil.mapGameToGameCreatedDTO(newGame);
    }

    private void chooseRandomlyStartingPlayer(Game game) {
        Random random = new Random();

        if (random.nextBoolean()) {
            game.setPlayerInTurn(game.getOwnerPlayer());
        } else {
            game.setPlayerInTurn(game.getOpponentPlayer());
        }
    }

    private Player createUserPlayer() {

        Player ownerPlayer = new Player();
        ownerPlayer.setUserId(OwnerUtil.getSimulationUser().getUserId());
        ownerPlayer.setFullName(OwnerUtil.getSimulationUser().getFullName());

        SpaceshipProtocol spaceshipProtocol = new SpaceshipProtocol();
        spaceshipProtocol.setHostname(OwnerUtil.getSimulationUser().getSpaceshipProtocol().getHostname());
        spaceshipProtocol.setPort(OwnerUtil.getSimulationUser().getSpaceshipProtocol().getPort());
        ownerPlayer.setProtocol(spaceshipProtocol);

        return ownerPlayer;
    }
}
