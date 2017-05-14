package com.xebia.exceptions;

import java.text.MessageFormat;

/**
 * Created by artur.skrzydlo on 2017-05-13.
 */
public class ShotOutOfBoardException extends RuntimeException {

    private static final String MESSAGE = "Shot {0} was outside of a game board !";
    private String shot = "";

    public ShotOutOfBoardException(String shot) {
        this.shot = shot;
    }

    @Override
    public String getMessage() {
        return MessageFormat.format(MESSAGE, shot);
    }
}
