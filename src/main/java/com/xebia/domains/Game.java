package com.xebia.domains;

import com.xebia.enums.GameStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */

@Entity
public class Game extends AbstractDomainClass {

    @Transient
    private static int GAME_COUNTER = 0;

    @Transient
    private static final String GAME_NAME_PREFIX = "match-";

    @Getter
    @Setter
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(nullable = false)
    private Player ownerPlayer;

    @Getter
    @Setter
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Player opponentPlayer;

    @Getter
    @Setter
    @ManyToOne
    private Player playerInTurn;

    @Getter
    @Setter
    @Column(nullable = false)
    private GameStatus status = GameStatus.ACTIVE;

    @Getter
    @Setter
    @ManyToOne
    private Player winningPlayer;

    @Column(unique = true)
    private String gameId;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setGameId() {
        this.gameId = GAME_NAME_PREFIX + GAME_COUNTER;
        GAME_COUNTER++;
    }
}
