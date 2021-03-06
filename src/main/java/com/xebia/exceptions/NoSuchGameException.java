package com.xebia.exceptions;

import java.text.MessageFormat;

/**
 * Created by artur.skrzydlo on 2017-05-14.
 */
public class NoSuchGameException extends Exception {

    private static final String MESSAGE_GAME_WITH_ID = "Game with id={0} doesn't exists";
    private static final String MESSAGE_UNDEFINED_GAME = "Game does not exists !";
    private String gameId;

    public NoSuchGameException(String game) {
        this.gameId = game;
    }

    public NoSuchGameException() {
    }

    @Override
    public String getMessage() {

        if (gameId != null) {
            return MessageFormat.format(MESSAGE_GAME_WITH_ID, gameId);
        }
        return MESSAGE_UNDEFINED_GAME;
    }
}
