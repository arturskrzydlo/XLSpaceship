package com.xebia.domains;

import com.xebia.enums.PlayerType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by artur.skrzydlo on 2017-05-10.
 */
@Entity
@Getter
@Setter
public class Player extends AbstractDomainClass {

    private String userId;
    private String fullName;
    private PlayerType playerType;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "player")
    private List<GameBoardPosition> playerGameBoard = new ArrayList<>();

    @Embedded
    private SpaceshipProtocol protocol;


    public void addAllGameBoardPosition(List<GameBoardPosition> position) {
        position.stream().forEach(gameBoardPosition -> {
            gameBoardPosition.setPlayer(this);
            playerGameBoard.add(gameBoardPosition);
        });
    }

}
