package com.xebia.domains;

import com.xebia.enums.GameStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
@Getter @Setter @Entity public class Game extends AbstractDomainClass {

    @ManyToOne Player ownerPlayer;

    @ManyToOne Player secondPlayer;

    @ManyToOne Player playerInTurn;

    GameStatus status;

}
