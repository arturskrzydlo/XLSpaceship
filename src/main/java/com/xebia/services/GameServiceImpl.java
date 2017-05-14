package com.xebia.services;

import com.xebia.domains.Game;
import com.xebia.domains.GameBoardPosition;
import com.xebia.domains.Player;
import com.xebia.domains.SpaceshipProtocol;
import com.xebia.dto.GameCreatedDTO;
import com.xebia.dto.PlayerDTO;
import com.xebia.dto.SalvoDTO;
import com.xebia.dto.SalvoResultDTO;
import com.xebia.enums.GameStatus;
import com.xebia.enums.PlayerType;
import com.xebia.services.gameboard.GameBoardService;
import com.xebia.util.DTOMapperUtil;
import com.xebia.util.OwnerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameRepoService gameRepoService;

    @Autowired
    private PlayerRepoService playerRepoService;

    @Autowired
    private GameBoardService gameBoardService;

    @Override
    public GameCreatedDTO createNewGame(PlayerDTO playerDTO) {

        Game newGame = new Game();

        newGame.setOwnerPlayer(getOrCreateOwnerPlayer());

        Player opponentPlayer = getOrCreateOpponentPlayer(playerDTO);
        opponentPlayer.addAllGameBoardPosition(gameBoardService.createGameBoard().getFieldsCollection());
        opponentPlayer.setPlayerType(PlayerType.OPPONENT);
        newGame.setOpponentPlayer(opponentPlayer);

        chooseRandomlyStartingPlayer(newGame);
        newGame.setStatus(GameStatus.ACTIVE);

        newGame = gameRepoService.saveOrUpdate(newGame);

        return DTOMapperUtil.mapGameToGameCreatedDTO(newGame);
    }

    private Player getOrCreateOwnerPlayer() {
        Player player = playerRepoService.findMyPlayer();
        if (player != null) {
            return player;
        } else {
            return createUserPlayer();
        }
    }

    private Player getOrCreateOpponentPlayer(PlayerDTO playerDTO) {
        Player player = playerRepoService.findOpponentPlayer(playerDTO);
        if (player != null) {
            return player;
        } else {
            return DTOMapperUtil.mapPlayerDTOToPlayer(playerDTO);
        }
    }


    //TODO: change gameboardposition to gameboard - to have for instance method of finding field
    @Override
    public SalvoResultDTO receiveSalvo(SalvoDTO salvoDTO, Integer gameId) {

        Game actualGame = gameRepoService.getById(gameId);
        List<GameBoardPosition> playerGameBoard = actualGame.getOwnerPlayer().getPlayerGameBoard();
     /*   SalvoResultDTO salvoResultDTO =  checkForHits(salvoDTO, playerGameBoard);*/
        return null;
    }

    //TODO: add exception of shot out of board game
/*    private SalvoResultDTO checkForHits(SalvoDTO salvoDTO, List<GameBoardPosition> playerGameBoard) throws ShotOutOfBoardException {

        Map<ShotDTO,ShotResult> shotResults = new HashMap<>();
        salvoDTO.getListOfShots().stream().forEach(shotDTO -> {

                String[] rowColumn = shotDTO.getField().split("x");
                Character row = rowColumn[0].charAt(0);
                Character column = rowColumn[1].charAt(0);

                GameBoardPosition foundPosition = playerGameBoard.stream()
                        .filter(gameBoardPosition -> gameBoardPosition.getColumn().equals(column) && gameBoardPosition.getRow().equals(row))
                        .findFirst().orElseThrow(()-> new ShotOutOfBoardException(shotDTO.getField()));





        });
    }*/

/*    private ShotResult changeGameBoardPositionStatusAfterShot(GameBoardPosition gameBoardPosition){

        if(gameBoardPosition.getSpaceship()!=null){
            gameBoardPosition.
        }
    }*/


    private void chooseRandomlyStartingPlayer(Game game) {
        Random random = new Random();

        if (random.nextBoolean()) {
            game.setPlayerInTurn(game.getOwnerPlayer());
        } else {
            game.setPlayerInTurn(game.getOpponentPlayer());
        }
    }

    private Player createUserPlayer() {

        Player ownerPlayer = new Player();
        ownerPlayer.setUserId(OwnerUtil.getSimulationUser().getUserId());
        ownerPlayer.setFullName(OwnerUtil.getSimulationUser().getFullName());

        SpaceshipProtocol spaceshipProtocol = new SpaceshipProtocol();
        spaceshipProtocol.setHostname(OwnerUtil.getSimulationUser().getSpaceshipProtocol().getHostname());
        spaceshipProtocol.setPort(OwnerUtil.getSimulationUser().getSpaceshipProtocol().getPort());
        ownerPlayer.setProtocol(spaceshipProtocol);
        ownerPlayer.setPlayerType(PlayerType.OWNER);
        ownerPlayer.addAllGameBoardPosition(gameBoardService.createGameBoard().getFieldsCollection());
        return ownerPlayer;
    }
}
