package com.xebia.domains;

import com.xebia.enums.GameStatus;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "game")
    @Where()
    private List<GameBoardPosition> ownerPlayerGameBoardPositions = new ArrayList<>();





    private GameStatus status;

}
