package com.xebia.controllers;

import com.xebia.dto.GameCreatedDTO;
import com.xebia.dto.PlayerDTO;
import com.xebia.dto.SalvoDTO;
import com.xebia.dto.SalvoResultDTO;
import com.xebia.exceptions.*;
import com.xebia.services.game.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public GameCreatedDTO createNewGame(@RequestBody PlayerDTO player) throws NotYourTurnException {
        return gameService.createNewGame(player);
    }

    @RequestMapping(value = "/game/{gameId}", method = RequestMethod.PUT)
    public ResponseEntity<SalvoResultDTO> receiveSalvo(@PathVariable String gameId, @RequestBody SalvoDTO salvo) throws NoSuchGameException, NotYourTurnException {
        SalvoResultDTO salvoResultDTO = gameService.receiveSalvo(salvo, gameId);
        return new ResponseEntity<>(salvoResultDTO, HttpStatus.OK);

    }

    @ExceptionHandler(value = {ShotOutOfBoardException.class, NotYourTurnException.class, NoSuchGameException.class, IncorretSalvoShotsAmountException.class, GameHasFinishedException.class})
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception ex) {
        ErrorResponse error = new ErrorResponse();
        error.setErrorCode(HttpStatus.UNPROCESSABLE_ENTITY.value());
        error.setMessage(ex.getMessage());
        return new ResponseEntity<ErrorResponse>(error, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
