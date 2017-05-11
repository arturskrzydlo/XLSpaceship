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

    private String fullName;

    private PlayerType playerType;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "player")
    List<Game> playerGames = new ArrayList<>();

    @Embedded
    private SpaceshipProtocol protocol;

    public void addGame(Game game) {
        playerGames.add(game);
    }

    public void removeGame(Game game) {
        playerGames.remove(game);
    }

}
