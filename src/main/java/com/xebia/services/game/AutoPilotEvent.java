package com.xebia.services.game;

import org.springframework.context.ApplicationEvent;

/**
 * Created by artur.skrzydlo on 2017-07-06.
 */
public class AutoPilotEvent extends ApplicationEvent {

    public AutoPilotEvent(Object source) {
        super(source);
    }

}
