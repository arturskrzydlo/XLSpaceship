package com.xebia.controllers;

import com.xebia.dto.*;
import com.xebia.exceptions.GameHasFinishedException;
import com.xebia.exceptions.NoSuchGameException;
import com.xebia.services.game.GameServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by artur.skrzydlo on 2017-05-10.
 */
@RequestMapping("/xl-spaceship/user")
@RestController
public class UserController {

    private Logger logger = LoggerFactory.getLogger(ProtocolController.class);

    @Autowired
    private GameServiceClient gameServiceClient;

    @RequestMapping(value = "/game/{gameId}/fire", method = RequestMethod.PUT)
    public SalvoResultDTO fireSalvo(@PathVariable String gameId, @RequestBody SalvoDTO salvo) throws NoSuchGameException, GameHasFinishedException {
        logger.info("Firing salvo " + salvo + "for a game " + gameId);
        SalvoResultDTO salvoResultDTO = gameServiceClient.fireSalvo(gameId, salvo);
        logger.info("Result of fired salvo is " + salvoResultDTO);
        return salvoResultDTO;
    }

    @RequestMapping(value = "/game/{gameId}", method = RequestMethod.GET)
    public GameStatusDTO getGameStatus(@PathVariable String gameId) throws NoSuchGameException {
        GameStatusDTO gameStatus = gameServiceClient.getGameStatus(gameId);
        return gameStatus;
    }

    @RequestMapping(value = "/game/new", method = RequestMethod.POST)
    public GameCreatedDTO challengeAnotherPlayerToGame(@RequestBody PlayerDTO playerDTO) {
        logger.info("Challenging player " + playerDTO + " for a game");
        GameCreatedDTO gameCreatedDTO = gameServiceClient.challengePlayerForAGame(playerDTO);
        logger.info("Game has been created : " + gameCreatedDTO);
        return gameCreatedDTO;
    }

    @RequestMapping(value = "/game/{gameId}/auto", method = RequestMethod.POST)
    public void turnOnAutopilot(@PathVariable String gameId) {
        logger.info("Autopilot for game " + gameId + "is on");
        gameServiceClient.turnOnAutopilot(gameId);
    }

    @RequestMapping(value = "/game/all", method = RequestMethod.GET)
    public List<GameDTO> getAllGames() {
        return gameServiceClient.getAllGames();
    }

    @RequestMapping(value = "/data", method = RequestMethod.GET)
    public PlayerDTO getOwnePlayerData() {
        return gameServiceClient.getOwnerPlayerData();
    }

}
