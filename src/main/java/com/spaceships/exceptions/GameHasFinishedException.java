package com.spaceships.exceptions;

import java.text.MessageFormat;

/**
 * Created by artur.skrzydlo on 2017-05-16.
 */
public class GameHasFinishedException extends Exception {

    private static final String MESSAGE = "Game has already finished. The game has been won by {0}. You can't shot anymore";
    private String winnerUserId;

    public GameHasFinishedException(String winnerUserId) {
        this.winnerUserId = winnerUserId;
    }

    @Override
    public String getMessage() {
        return MessageFormat.format(MESSAGE, winnerUserId);

    }
}
