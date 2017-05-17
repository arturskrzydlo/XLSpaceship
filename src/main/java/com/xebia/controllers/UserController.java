package com.xebia.controllers;

import com.xebia.dto.*;
import com.xebia.exceptions.GameHasFinishedException;
import com.xebia.exceptions.NoSuchGameException;
import com.xebia.services.game.GameServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by artur.skrzydlo on 2017-05-10.
 */
@RequestMapping("/xl-spaceship/user")
@RestController
public class UserController {

    @Autowired
    private GameServiceClient gameServiceClient;

    @RequestMapping(value = "/game/{gameId}/fire", method = RequestMethod.PUT)
    public SalvoResultDTO fireSalvo(@PathVariable Integer gameId, @RequestBody SalvoDTO salvo) throws NoSuchGameException, GameHasFinishedException {
        return gameServiceClient.fireSalvo(gameId, salvo);
    }

    @RequestMapping(value = "/game/{gameId}", method = RequestMethod.GET)
    public GameStatusDTO getGameStatus(@PathVariable Integer gameId) throws NoSuchGameException {
        return gameServiceClient.getGameStatus(gameId);
    }

    @RequestMapping(value = "/game/new", method = RequestMethod.POST)
    public GameCreatedDTO challengeAnotherPlayerToGame(@RequestBody PlayerDTO playerDTO) {
        return gameServiceClient.challengePlayerForAGame(playerDTO);
    }

    @RequestMapping(value = "/game/{gameId}/auto", method = RequestMethod.POST)
    public void turnOnAutopilot(@PathVariable Integer gameId) {
        gameServiceClient.turnOnAutopilot(gameId);
    }

}
