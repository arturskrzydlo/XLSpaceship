package com.xebia.controllers;

import com.xebia.dto.GameDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by artur.skrzydlo on 2017-05-10.
 */
@RequestMapping("/xl-spaceship/protocol")
@RestController
public class ProtocolController {

    @RequestMapping(value = "/game/new" ,method = RequestMethod.POST)
    public ResponseEntity<GameDTO> createNewGame(@RequestBody GameDTO game){
        return new ResponseEntity<GameDTO>(game, HttpStatus.CREATED);
    }
}
