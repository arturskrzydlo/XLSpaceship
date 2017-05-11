package com.xebia.domains;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
@Getter @Setter @Entity public class GameBoardPosition extends AbstractDomainClass {

    private Character row;
    private Character column;
    private Boolean isHit;

    @ManyToOne Spaceship spaceship;

}