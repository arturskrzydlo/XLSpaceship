package com.xebia.domains;

import com.xebia.enums.GameStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
@Getter
@Setter
@Entity
public class Game extends AbstractDomainClass {


    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(nullable = false)
    private Player ownerPlayer;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Player opponentPlayer;

    @ManyToOne
    private Player playerInTurn;

    @Column(nullable = false)
    private GameStatus status = GameStatus.ACTIVE;

    @ManyToOne
    private Player winningPlayer;


}
