package com.spaceships.util;

import com.spaceships.domains.Game;
import com.spaceships.domains.Player;
import com.spaceships.domains.SpaceshipProtocol;
import com.spaceships.dto.GameCreatedDTO;
import com.spaceships.dto.GameDTO;
import com.spaceships.dto.PlayerDTO;
import com.spaceships.dto.SpaceshipProtocolDTO;

import javax.validation.constraints.NotNull;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public class DTOMapperUtil {

    public static PlayerDTO mapPlayerToPlayerDTO(@NotNull Player player) {


        PlayerDTO result = new PlayerDTO();
        result.setUserId(player.getUserId());
        result.setFullName(player.getFullName());

        if (player.getProtocol() != null) {
            SpaceshipProtocolDTO spaceshipProtocolDTO = new SpaceshipProtocolDTO();
            spaceshipProtocolDTO.setHostname(player.getProtocol().getHostname());
            spaceshipProtocolDTO.setPort(player.getProtocol().getPort());

            result.setSpaceshipProtocol(spaceshipProtocolDTO);
        }

        return result;
    }

    public static Player mapPlayerDTOToPlayer(@NotNull PlayerDTO playerDTO) {

        Player result = new Player();
        result.setUserId(playerDTO.getUserId());
        result.setFullName(playerDTO.getFullName());

        if (playerDTO.getSpaceshipProtocol() != null) {
            SpaceshipProtocol spaceshipProtocol = new SpaceshipProtocol();
            spaceshipProtocol.setHostname(playerDTO.getSpaceshipProtocol().getHostname());
            spaceshipProtocol.setPort(playerDTO.getSpaceshipProtocol().getPort());

            result.setProtocol(spaceshipProtocol);
        }

        return result;

    }

    public static Game mapGameDTOToGame(@NotNull GameDTO gameDTO) {

        Game result = new Game();
        result.setPlayerInTurn(mapPlayerDTOToPlayer(gameDTO.getStartingPlayer()));
        result.setOpponentPlayer(mapPlayerDTOToPlayer(gameDTO.getOpponent()));
        result.setGameId(gameDTO.getGameId());

        return result;

    }

    public static GameDTO mapGameToGameDTO(@NotNull Game game) {

        GameDTO result = new GameDTO();
        result.setGameId(game.getGameId());
        result.setOpponent(mapPlayerToPlayerDTO(game.getOpponentPlayer()));
        result.setStartingPlayer(mapPlayerToPlayerDTO(game.getPlayerInTurn()));

        return result;

    }

    public static GameCreatedDTO mapGameToGameCreatedDTO(@NotNull Game game) {

        GameCreatedDTO result = new GameCreatedDTO();
        result.setGameId(game.getGameId());
        if (game.getOwnerPlayer() != null) {
            result.setOpponentId(game.getOwnerPlayer().getUserId());
            result.setFullName(game.getOwnerPlayer().getFullName());
        }
        if (game.getPlayerInTurn() != null) {
            result.setStartingPlayerId(game.getPlayerInTurn().getUserId());
        }

        return result;

    }


}
