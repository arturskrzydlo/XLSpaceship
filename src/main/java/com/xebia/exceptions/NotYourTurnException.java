package com.xebia.exceptions;

/**
 * Created by artur.skrzydlo on 2017-05-14.
 */
public class NotYourTurnException extends RuntimeException {

    private static final String MESSAGE = "Player can't shot twice or more in a row. Wait for opponent salvo";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
