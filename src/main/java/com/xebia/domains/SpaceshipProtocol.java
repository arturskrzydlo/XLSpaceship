package com.xebia.domains;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

/**
 * Created by artur.skrzydlo on 2017-05-10.
 */

@Getter @Setter @Embeddable
public class SpaceshipProtocol extends AbstractDomainClass{

    private String hostname;
    private Integer port;
}
