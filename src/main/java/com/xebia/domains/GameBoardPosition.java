package com.xebia.domains;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
@Getter
@Setter
@EqualsAndHashCode
@Entity
public class GameBoardPosition extends AbstractDomainClass {

    private Character row;
    private Character column;
    private Boolean isHit;
    @ManyToOne
    private Game game;

    @ManyToOne(cascade = CascadeType.ALL)
    private Spaceship spaceship;

    @ManyToOne(cascade = CascadeType.ALL)
    private Player player;

}
