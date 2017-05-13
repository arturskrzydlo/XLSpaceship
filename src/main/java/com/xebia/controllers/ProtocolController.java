package com.xebia.controllers;

import com.xebia.dto.GameCreatedDTO;
import com.xebia.dto.PlayerDTO;
import com.xebia.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Created by artur.skrzydlo on 2017-05-10.
 */
@RequestMapping("/xl-spaceship/protocol")
@RestController
public class ProtocolController {

    //TODO: add logging
    @Autowired
    private GameService gameService;


    @RequestMapping(value = "/game/new", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public GameCreatedDTO createNewGame(@RequestBody PlayerDTO player) {


        return gameService.createNewGame(player);
    }
}
