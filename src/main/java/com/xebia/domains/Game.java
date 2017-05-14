package com.xebia.domains;

import com.xebia.enums.GameStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
@Getter
@Setter
@Entity
public class Game extends AbstractDomainClass {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Player ownerPlayer;

    @ManyToOne(cascade = CascadeType.ALL)
    private Player opponentPlayer;

    @ManyToOne
    private Player playerInTurn;

    private GameStatus status;

}
