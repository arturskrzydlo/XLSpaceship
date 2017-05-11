package com.xebia.domains;

import com.xebia.enums.SpaceshipType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
@Getter @Setter @Entity public class Spaceship extends AbstractDomainClass {

    private Boolean isAlive;

    private SpaceshipType type;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "spaceship")
    List<GameBoardPosition> positions = new ArrayList<>();

    public void addSpaceshipPosition(GameBoardPosition gameBoardPosition) {
        positions.add(gameBoardPosition);
        gameBoardPosition.setSpaceship(this);
    }

    public void removeSpaceshiptPosition(GameBoardPosition gameBoardPosition) {
        gameBoardPosition.setSpaceship(null);
        positions.remove(gameBoardPosition);
    }
}
