package com.xebia.services;

import com.xebia.domains.Game;
import com.xebia.domains.GameBoardPosition;
import com.xebia.domains.Player;
import com.xebia.domains.SpaceshipProtocol;
import com.xebia.dto.*;
import com.xebia.enums.*;
import com.xebia.exceptions.NoSuchGameException;
import com.xebia.exceptions.ShotOutOfBoardException;
import com.xebia.services.gameboard.GameBoard;
import com.xebia.services.gameboard.GameBoardService;
import com.xebia.util.DTOMapperUtil;
import com.xebia.util.OwnerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
@Service
//TODO: handling wrong "order" of request
public class GameServiceImpl implements GameService {

    @Autowired
    private GameRepoService gameRepoService;

    @Autowired
    private PlayerRepoService playerRepoService;

    @Autowired
    private GameBoardService gameBoardService;

    @Autowired
    private GameBoardRepoService gameBoardRepoService;

    @Override
    public GameCreatedDTO createNewGame(PlayerDTO playerDTO) {

        Game newGame = new Game();
        newGame = createGamePlayers(newGame, playerDTO);
        chooseRandomlyStartingPlayer(newGame);
        newGame.setStatus(GameStatus.ACTIVE);
        newGame = gameRepoService.saveOrUpdate(newGame);

        gameBoardRepoService.batchSave(createGameBoardForOwnerPlayer(newGame).getFieldsCollection());
        gameBoardRepoService.batchSave(createGameBoardForOpponentPlayer(newGame).getFieldsCollection());

        return DTOMapperUtil.mapGameToGameCreatedDTO(newGame);
    }


    //TODO: change gameboardposition to gameboard - to have for example method of finding field
    @Override
    public SalvoResultDTO receiveSalvo(SalvoDTO salvoDTO, Integer gameId) throws ShotOutOfBoardException, NoSuchGameException {

        Game actualGame = gameRepoService.getById(gameId);
        if (actualGame == null || actualGame.getStatus().equals(GameStatus.FINISHED)) {
            throw new NoSuchGameException(gameId);
        }
        SalvoResultDTO salvoResultDTO = checkForHits(salvoDTO, gameBoardRepoService.getOwnerGameBoardByGame(gameId));
        return salvoResultDTO;
    }

    private SalvoResultDTO checkForHits(SalvoDTO salvoDTO, List<GameBoardPosition> playerGameBoard) throws ShotOutOfBoardException {

        Map<ShotDTO, ShotResult> shotResults = new HashMap<>();
        List<GameBoardPosition> updatedPositions = new ArrayList<>();

        salvoDTO.getListOfShots().stream().forEach(shotDTO -> {

            String[] rowColumn = shotDTO.getField().split("x");
            Character row = rowColumn[0].toLowerCase().charAt(0);
            Character column = rowColumn[1].toLowerCase().charAt(0);

            GameBoardPosition foundPosition = playerGameBoard.stream()
                    .filter(gameBoardPosition -> gameBoardPosition.getColumn().equals(column) && gameBoardPosition.getRow().equals(row))
                    .findFirst().orElseThrow(() -> new ShotOutOfBoardException(shotDTO.getField()));


            updatedPositions.add(foundPosition);
            shotResults.put(shotDTO, changeOwnerGameBoardPositionStatusAfterShot(foundPosition, playerGameBoard));

        });

        gameBoardRepoService.batchSave(updatedPositions);
        return createSalvoResult(playerGameBoard, shotResults);
    }

    private SalvoResultDTO createSalvoResult(List<GameBoardPosition> playerGameBoard, Map<ShotDTO, ShotResult> shotResults) {

        SalvoResultDTO salvoResultDTO = new SalvoResultDTO();
        salvoResultDTO.setSalvoResult(shotResults);

        GameStatusDTO gameStatusDTO = new GameStatusDTO();
        GameStatus gameStatus = checkGameStatus(playerGameBoard);

        GameBoardPosition anyField = playerGameBoard.get(0);

        if (gameStatus.equals(GameStatus.ACTIVE)) {

            gameStatusDTO.setPlayerInTurn(getNextTurnPlayer(anyField.getPlayer(), anyField.getGame()).getUserId());

        } else if (gameStatus.equals(GameStatus.FINISHED)) {

            gameStatusDTO.setWinnngPlayer(getNextTurnPlayer(anyField.getPlayer(), anyField.getGame()).getUserId());
        }

        salvoResultDTO.setGameStatus(gameStatusDTO);

        return salvoResultDTO;
    }

    private Player getNextTurnPlayer(Player actualPlayer, Game game) {

        if (actualPlayer.equals(game.getOpponentPlayer())) {
            return game.getOwnerPlayer();
        } else {
            return game.getOpponentPlayer();
        }
    }

    private GameStatus checkGameStatus(List<GameBoardPosition> playerGameBoard) {

        if (isPlayerSpaceshipFleetDestroyed(playerGameBoard)) {
            Game game = gameRepoService.getById(playerGameBoard.get(0).getGame().getId());
            game.setStatus(GameStatus.FINISHED);
            gameRepoService.saveOrUpdate(game);
            return GameStatus.FINISHED;
        }

        return GameStatus.ACTIVE;
    }

    private ShotResult changeOwnerGameBoardPositionStatusAfterShot(GameBoardPosition gameBoardPosition, List<GameBoardPosition> playerGameBoard) {

        if (gameBoardPosition.getSpaceship() != null) {

            if (gameBoardPosition.getHitStatus().equals(HitStatus.HIT)) {
                return ShotResult.MISS;
            }
            gameBoardPosition.setHitStatus(HitStatus.HIT);
            if (isSpaceshipDestroyed(playerGameBoard, gameBoardPosition.getSpaceship().getType())) {
                gameBoardPosition.getSpaceship().setAlive(false);
                return ShotResult.KILL;
            }
            return ShotResult.HIT;
        } else {
            gameBoardPosition.setHitStatus(HitStatus.MISS);
            return ShotResult.MISS;
        }
    }

    private boolean isSpaceshipDestroyed(List<GameBoardPosition> playerGameBoard, SpaceshipType spaceshipType) {

        long numberOfHitFields = playerGameBoard.stream()
                .filter(gameBoardPosition -> gameBoardPosition.getSpaceship() != null)
                .filter(gameBoardPosition -> gameBoardPosition.getSpaceship().getType().equals(spaceshipType))
                .filter(gameBoardPosition -> gameBoardPosition.getHitStatus().equals(HitStatus.HIT))
                .count();

        return spaceshipType.getNumberOfFields() == numberOfHitFields;

    }

    private boolean isPlayerSpaceshipFleetDestroyed(List<GameBoardPosition> playerGameBoard) {

        return !playerGameBoard.stream()
                .filter(gameBoardPosition -> gameBoardPosition.getSpaceship() != null)
                .anyMatch(gameBoardPosition -> gameBoardPosition.getSpaceship().isAlive());
    }

    private GameBoard createGameBoardForOwnerPlayer(Game newGame) {

        GameBoard gameBoard = gameBoardService.createGameBoard();
        gameBoard.getFieldsCollection().stream().forEach(gameBoardPosition -> {
            gameBoardPosition.setPlayer(newGame.getOwnerPlayer());
            gameBoardPosition.setGame(newGame);
        });

        return gameBoard;
    }

    private GameBoard createGameBoardForOpponentPlayer(Game newGame) {

        GameBoard gameBoard = gameBoardService.createEmptyGameBoard();
        gameBoard.getFieldsCollection().stream().forEach(gameBoardPosition -> {
            gameBoardPosition.setPlayer(newGame.getOpponentPlayer());
            gameBoardPosition.setGame(newGame);
        });

        return gameBoard;
    }


    private Game createGamePlayers(Game game, PlayerDTO playerDTO) {

        game.setOwnerPlayer(getOrCreateOwnerPlayer());

        Player opponentPlayer = getOrCreateOpponentPlayer(playerDTO);
        opponentPlayer.setPlayerType(PlayerType.OPPONENT);
        game.setOpponentPlayer(opponentPlayer);

        return game;
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
        return ownerPlayer;
    }
}
