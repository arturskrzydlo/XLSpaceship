package com.xebia.controllers;

import com.xebia.dto.GameStatusDTO;
import com.xebia.dto.SalvoDTO;
import com.xebia.dto.SalvoResultDTO;
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
    public SalvoResultDTO fireSalvo(@PathVariable Integer gameId, @RequestBody SalvoDTO player) {
        return gameServiceClient.fireSalvo(gameId, player);
    }

    @RequestMapping(value = "/game/{gameId}", method = RequestMethod.GET)
    public GameStatusDTO getGameStatus(@PathVariable Integer gameId) throws NoSuchGameException {
        return gameServiceClient.getGameStatus(gameId);
    }

}
