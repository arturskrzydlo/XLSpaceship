package com.xebia.exceptions;

import java.text.MessageFormat;

/**
 * Created by artur.skrzydlo on 2017-05-14.
 */
public class IncorretSalvoShotsAmountException extends RuntimeException {

    private static final String MESSAGE = "Incorrect number of shots. In salvo was {0} shots, but you have {1} Spaceships alive";

    private int numberOfShots;
    private int numberOfAliveSpaceships;

    public IncorretSalvoShotsAmountException(int numberOfShots, int numberOfAliveSpaceships) {
        this.numberOfShots = numberOfShots;
        this.numberOfAliveSpaceships = numberOfAliveSpaceships;
    }

    @Override
    public String getMessage() {
        return MessageFormat.format(MESSAGE, numberOfShots, numberOfAliveSpaceships);
    }
}
