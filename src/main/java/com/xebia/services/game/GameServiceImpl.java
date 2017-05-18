package com.xebia.services.game;

import com.xebia.domains.Game;
import com.xebia.domains.GameBoardPosition;
import com.xebia.domains.Player;
import com.xebia.domains.SpaceshipProtocol;
import com.xebia.dto.*;
import com.xebia.enums.GameStatus;
import com.xebia.enums.HitStatus;
import com.xebia.enums.PlayerType;
import com.xebia.enums.SpaceshipType;
import com.xebia.exceptions.NoSuchGameException;
import com.xebia.exceptions.NotYourTurnException;
import com.xebia.exceptions.ShotOutOfBoardException;
import com.xebia.services.gameboard.GameBoard;
import com.xebia.services.gameboard.GameBoardService;
import com.xebia.services.reposervices.game.GameRepoService;
import com.xebia.services.reposervices.gameboard.GameBoardRepoService;
import com.xebia.services.reposervices.player.PlayerRepoService;
import com.xebia.thread.AutoFireResponseThread;
import com.xebia.util.DTOMapperUtil;
import com.xebia.util.OwnerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

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

    @Autowired
    private GameBoardRepoService gameBoardRepoService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public GameCreatedDTO createNewGame(PlayerDTO playerDTO) throws NotYourTurnException {


        Game newGame = new Game();
        newGame = createGamePlayers(newGame, playerDTO);
        chooseRandomlyStartingPlayer(newGame);
        newGame.setStatus(GameStatus.ACTIVE);
        newGame.setGameId();
        newGame = gameRepoService.saveOrUpdate(newGame);

        gameBoardRepoService.batchSave(createGameBoardForOwnerPlayer(newGame).getFieldsCollection());
        gameBoardRepoService.batchSave(createGameBoardForOpponentPlayer(newGame).getFieldsCollection());

        return DTOMapperUtil.mapGameToGameCreatedDTO(newGame);
    }


    //TODO: change gameboardposition to gameboard - to have for example method of finding field
    @Override
    public SalvoResultDTO receiveSalvo(SalvoDTO salvoDTO, String gameId) throws ShotOutOfBoardException, NoSuchGameException, NotYourTurnException {

        Game actualGame = gameRepoService.getByGameId(gameId);

        validateInput(actualGame);
        SalvoResultDTO salvoResultDTO = fireShotsOnOwnerGameboard(salvoDTO, gameBoardRepoService.getOwnerGameBoardByGame(gameId));

        if (salvoResultDTO.getGameStatus().getPlayerInTurn() == null) {
            actualGame.setStatus(GameStatus.FINISHED);
            actualGame.setWinningPlayer(actualGame.getOpponentPlayer());
            actualGame.getOwnerPlayer().setAutopilot(false);
        } else {

            actualGame.setPlayerInTurn(actualGame.getOwnerPlayer());
        }

        gameRepoService.saveOrUpdate(actualGame);
        if (actualGame.getOwnerPlayer().getAutopilot()) {

            AutoFireResponseThread autoFireResponseThread = new AutoFireResponseThread();
            autoFireResponseThread.setGameId(actualGame.getGameId());
            autoFireResponseThread.setOpponent(DTOMapperUtil.mapPlayerToPlayerDTO(actualGame.getOwnerPlayer()));
            autoFireResponseThread.setOpponentPlayerGameBoard(gameBoardRepoService.getOpponentPlayerByGame(actualGame.getGameId(), actualGame.getOpponentPlayer().getId()));
            autoFireResponseThread.setOwnerPlayerGameboard(gameBoardRepoService.getOwnerGameBoardByGame(gameId));
            autoFireResponseThread.setRestTemplate(restTemplate);
            autoFireResponseThread.start();
        }
        return salvoResultDTO;
    }


    //TODO: add winner column to game table
    @Override
    public void updateGameAfterYourSalvo(SalvoResultDTO salvoResultDTO, String gameId) {
        Game game = gameRepoService.getByGameId(gameId);

        fireShotsOnOpponentGameboard(salvoResultDTO, gameBoardRepoService.getOpponentPlayerByGame(gameId, game.getOpponentPlayer().getId()));
        if (salvoResultDTO.getGameStatus().getPlayerInTurn() == null) {
            game.setStatus(GameStatus.FINISHED);
            game.setWinningPlayer(game.getOwnerPlayer());
            game.getOwnerPlayer().setAutopilot(false);
            gameRepoService.saveOrUpdate(game);
        }
        if (salvoResultDTO.getGameStatus().getWinningPlayer() == null) {
            game.setPlayerInTurn(getGamePlayerByUserId(game, salvoResultDTO.getGameStatus().getPlayerInTurn()));
        }
        gameRepoService.saveOrUpdate(game);
    }

    private Player getGamePlayerByUserId(Game game, String playerInTurn) {
        if (game.getOpponentPlayer().getUserId().equals(playerInTurn)) {
            return game.getOpponentPlayer();
        }

        return game.getOwnerPlayer();
    }

    private void validateInput(Game actualGame) throws NoSuchGameException, NotYourTurnException {


        if (actualGame == null) {
            throw new NoSuchGameException();
        }

        if (actualGame != null && actualGame.getPlayerInTurn().equals(actualGame.getOwnerPlayer())) {
            throw new NotYourTurnException();
        }

    }


    //TODO: extract common parts and make one method instead of two
    private void fireShotsOnOpponentGameboard(SalvoResultDTO salvoResultDTO, List<GameBoardPosition> playerGameBoard) {

        Map<String, HitStatus> shotResults = new HashMap<>();
        List<GameBoardPosition> updatedPositions = new ArrayList<>();

        salvoResultDTO.getSalvoResult().entrySet().stream().forEach(shotStringShotResultEntry -> {

            String shot = shotStringShotResultEntry.getKey();
            HitStatus shotResult = shotStringShotResultEntry.getValue();

            String[] rowColumn = shot.split("x");
            Character row = rowColumn[0].toLowerCase().charAt(0);
            Character column = rowColumn[1].toLowerCase().charAt(0);

            GameBoardPosition foundPosition = playerGameBoard.stream()
                    .filter(gameBoardPosition -> gameBoardPosition.getColumn().equals(column) && gameBoardPosition.getRow().equals(row))
                    .findFirst().get();

            foundPosition.setHitStatus(shotResult);
            updatedPositions.add(foundPosition);

        });

        gameBoardRepoService.batchSave(updatedPositions);
    }


    private SalvoResultDTO fireShotsOnOwnerGameboard(SalvoDTO salvoDTO, List<GameBoardPosition> playerGameBoard) throws ShotOutOfBoardException {

        Map<String, HitStatus> shotResults = new HashMap<>();
        List<GameBoardPosition> updatedPositions = new ArrayList<>();

        salvoDTO.getListOfShots().stream().forEach(shotDTO -> {

            String[] rowColumn = shotDTO.split("x");
            Character row = rowColumn[0].toLowerCase().charAt(0);
            Character column = rowColumn[1].toLowerCase().charAt(0);

            GameBoardPosition foundPosition = playerGameBoard.stream()
                    .filter(gameBoardPosition -> gameBoardPosition.getColumn().equals(column) && gameBoardPosition.getRow().equals(row))
                    .findFirst().orElseThrow(() -> new ShotOutOfBoardException(shotDTO));


            updatedPositions.add(foundPosition);
            shotResults.put(shotDTO, changeOwnerGameBoardPositionStatusAfterShot(foundPosition, playerGameBoard));

        });

        gameBoardRepoService.batchSave(updatedPositions);
        return createSalvoResult(playerGameBoard, shotResults);
    }

    private SalvoResultDTO createSalvoResult(List<GameBoardPosition> playerGameBoard, Map<String, HitStatus> shotResults) {

        SalvoResultDTO salvoResultDTO = new SalvoResultDTO();
        salvoResultDTO.setSalvoResult(shotResults);

        GamePropertiesDTO gamePropertiesDTO = new GamePropertiesDTO();
        GameStatus gameStatus = checkGameStatus(playerGameBoard);

        GameBoardPosition anyField = playerGameBoard.get(0);

        if (gameStatus.equals(GameStatus.ACTIVE)) {

            gamePropertiesDTO.setPlayerInTurn(anyField.getPlayer().getUserId());

        } else if (gameStatus.equals(GameStatus.FINISHED)) {

            gamePropertiesDTO.setWinningPlayer(anyField.getGame().getOpponentPlayer().getUserId());
        }

        salvoResultDTO.setGameStatus(gamePropertiesDTO);

        return salvoResultDTO;
    }

    private GameStatus checkGameStatus(List<GameBoardPosition> playerGameBoard) {

        if (isPlayerSpaceshipFleetDestroyed(playerGameBoard)) {
            Game game = gameRepoService.getByGameId(playerGameBoard.get(0).getGame().getGameId());
            game.setStatus(GameStatus.FINISHED);
            gameRepoService.saveOrUpdate(game);
            return GameStatus.FINISHED;
        }

        return GameStatus.ACTIVE;
    }

    private HitStatus changeOwnerGameBoardPositionStatusAfterShot(GameBoardPosition gameBoardPosition, List<GameBoardPosition> playerGameBoard) {

        if (gameBoardPosition.getSpaceship() != null) {

            if (gameBoardPosition.getHitStatus().equals(HitStatus.HIT)) {
                return HitStatus.MISS;
            }
            gameBoardPosition.setHitStatus(HitStatus.HIT);
            if (isSpaceshipDestroyed(playerGameBoard, gameBoardPosition.getSpaceship().getType())) {
                gameBoardPosition.getSpaceship().setAlive(false);
                return HitStatus.KILL;
            }
            return HitStatus.HIT;
        } else {
            gameBoardPosition.setHitStatus(HitStatus.MISS);
            return HitStatus.MISS;
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
