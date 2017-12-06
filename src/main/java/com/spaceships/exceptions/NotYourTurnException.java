package com.spaceships.exceptions;

/**
 * Created by artur.skrzydlo on 2017-05-14.
 */
public class NotYourTurnException extends Exception {

    public static final String MESSAGE = "Not your turn. Wait for opponent salvo";

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
