package com.spaceships.services.game;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * Created by artur.skrzydlo on 2017-07-06.
 */
@Component
public class AutoPilotEventPublisher implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    public void publish(AutoPilotEvent autoPilotEvent) {
        this.publisher.publishEvent(autoPilotEvent);
    }

}
