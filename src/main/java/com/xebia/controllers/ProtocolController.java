package com.xebia.controllers;

import com.xebia.dto.GameDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Created by artur.skrzydlo on 2017-05-10.
 */
@RequestMapping("/xl-spaceship/protocol")
@RestController
public class ProtocolController {

    @RequestMapping(value = "/game/new", method = RequestMethod.POST) @ResponseStatus(HttpStatus.CREATED)
    public GameDTO createNewGame(@RequestBody GameDTO game) {

        return game;
    }
}