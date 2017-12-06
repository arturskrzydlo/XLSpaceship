package com.spaceships.services;

import com.spaceships.domains.Game;
import com.spaceships.domains.GameBoardPosition;
import com.spaceships.domains.Spaceship;
import com.spaceships.dto.*;
import com.spaceships.enums.GameStatus;
import com.spaceships.enums.HitStatus;
import com.spaceships.enums.SpaceshipType;
import com.spaceships.exceptions.NoSuchGameException;
import com.spaceships.exceptions.NotYourTurnException;
import com.spaceships.services.game.GameServiceImpl;
import com.spaceships.services.gameboard.GameBoard;
import com.spaceships.services.gameboard.GameBoardService;
import com.spaceships.services.reposervices.game.GameRepoService;
import com.spaceships.services.reposervices.gameboard.GameBoardRepoService;
import com.spaceships.services.reposervices.player.PlayerRepoService;
import com.spaceships.util.DTOMapperUtil;
import com.spaceships.util.OwnerUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by artur.skrzydlo on 2017-05-13.
 */
@RunWith(SpringRunner.class)
public class GameServiceImplTest {


    @Mock
    private GameRepoService gameRepoService;

    @Mock
    private GameBoardRepoService gameBoardRepoService;

    @Mock
    private GameBoardService gameBoardService;

    @Mock
    private PlayerRepoService playerRepoService;

    @InjectMocks
    private GameServiceImpl gameService;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void newGameHasBeenCreated() throws NotYourTurnException {

        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setUserId("xebialabs-1");
        playerDTO.setFullName("XebiaLabs Opponent");
        playerDTO.setSpaceshipProtocol(new SpaceshipProtocolDTO("127.0.0.1", 9001));

        Game game = new Game();
        game.setPlayerInTurn(DTOMapperUtil.mapPlayerDTOToPlayer(playerDTO));
        game.setOpponentPlayer(DTOMapperUtil.mapPlayerDTOToPlayer(playerDTO));
        game.setStatus(GameStatus.ACTIVE);
        game.setOwnerPlayer(DTOMapperUtil.mapPlayerDTOToPlayer(playerDTO));
        game.setId(1);
        game.setGameId("match-1");

        Mockito.when(playerRepoService.findMyPlayer()).thenReturn(DTOMapperUtil.mapPlayerDTOToPlayer(playerDTO));
        Mockito.when(playerRepoService.findOpponentPlayer(Matchers.any())).thenReturn(DTOMapperUtil.mapPlayerDTOToPlayer(playerDTO));
        Mockito.when(gameBoardService.createGameBoard()).thenReturn(new GameBoard());
        Mockito.when(gameBoardService.createEmptyGameBoard()).thenReturn(new GameBoard());
        Mockito.when(gameBoardRepoService.batchSave(Matchers.anyList())).thenReturn(new ArrayList<GameBoardPosition>());
        Mockito.when(gameRepoService.saveOrUpdate(Matchers.any())).thenReturn(game);
        GameCreatedDTO gameDTO = gameService.createNewGame(playerDTO);

        Assert.assertNotNull(gameDTO);
        Assert.assertNotNull(gameDTO.getFullName());
        Assert.assertFalse(gameDTO.getFullName().isEmpty());

        Assert.assertNotNull(gameDTO.getOpponentId());
        Assert.assertFalse(gameDTO.getOpponentId().isEmpty());

        Assert.assertNotNull(gameDTO.getGameId());

        Assert.assertNotNull(gameDTO.getStartingPlayerId());
        Assert.assertFalse(gameDTO.getStartingPlayerId().isEmpty());
        Assert.assertEquals(gameDTO.getStartingPlayerId(), game.getPlayerInTurn().getUserId());
    }

    @Test
    public void playersHasBeanCreatedIfTheyDontExists() throws NotYourTurnException {

        PlayerDTO opponentPlayer = new PlayerDTO();
        opponentPlayer.setUserId("xebialabs-1");
        opponentPlayer.setFullName("XebiaLabs Opponent");
        opponentPlayer.setSpaceshipProtocol(new SpaceshipProtocolDTO("127.0.0.1", 9001));

        PlayerDTO myPlayer = OwnerUtil.getSimulationUser();

        Game newGame = new Game();
        newGame.setOwnerPlayer(DTOMapperUtil.mapPlayerDTOToPlayer(myPlayer));
        newGame.setOpponentPlayer(DTOMapperUtil.mapPlayerDTOToPlayer(opponentPlayer));

        Mockito.when(playerRepoService.findMyPlayer()).thenReturn(null);
        Mockito.when(playerRepoService.findOpponentPlayer(Matchers.any())).thenReturn(null);
        Mockito.when(gameRepoService.saveOrUpdate(Matchers.any())).thenReturn(newGame);
        Mockito.when(gameBoardService.createGameBoard()).thenReturn(new GameBoard());
        Mockito.when(gameBoardService.createEmptyGameBoard()).thenReturn(new GameBoard());
        Mockito.when(gameBoardRepoService.batchSave(Matchers.anyList())).thenReturn(new ArrayList<GameBoardPosition>());

        ArgumentCaptor<Game> gameArgumentCaptor = ArgumentCaptor.forClass(Game.class);

        GameCreatedDTO gameDTO = gameService.createNewGame(opponentPlayer);

        Mockito.verify(gameRepoService, Mockito.times(1)).saveOrUpdate(gameArgumentCaptor.capture());

        Game argumentGame = gameArgumentCaptor.getValue();

        Assert.assertNotNull(argumentGame.getOpponentPlayer());
        Assert.assertNotNull(argumentGame.getOwnerPlayer());

        Assert.assertEquals(argumentGame.getOwnerPlayer().getId(), null);
        Assert.assertEquals(argumentGame.getOpponentPlayer().getId(), null);

    }

    @Test
    public void playersHasBeanRetrievedIfTheyExists() throws NotYourTurnException {

        Game newGame = createSampleGame();
        newGame.getOwnerPlayer().setId(1);
        newGame.getOpponentPlayer().setId(2);

        Mockito.when(playerRepoService.findMyPlayer()).thenReturn(newGame.getOwnerPlayer());
        Mockito.when(playerRepoService.findOpponentPlayer(Matchers.any())).thenReturn(newGame.getOpponentPlayer());
        Mockito.when(gameRepoService.saveOrUpdate(Matchers.any())).thenReturn(newGame);
        Mockito.when(gameBoardService.createGameBoard()).thenReturn(new GameBoard());
        Mockito.when(gameBoardService.createEmptyGameBoard()).thenReturn(new GameBoard());
        Mockito.when(gameBoardRepoService.batchSave(Matchers.anyList())).thenReturn(new ArrayList<GameBoardPosition>());

        ArgumentCaptor<Game> gameArgumentCaptor = ArgumentCaptor.forClass(Game.class);

        GameCreatedDTO gameDTO = gameService.createNewGame(DTOMapperUtil.mapPlayerToPlayerDTO(newGame.getOpponentPlayer()));

        Mockito.verify(gameRepoService, Mockito.times(1)).saveOrUpdate(gameArgumentCaptor.capture());
        Mockito.verify(playerRepoService, Mockito.times(1)).findMyPlayer();
        Mockito.verify(playerRepoService, Mockito.times(1)).findOpponentPlayer(DTOMapperUtil.mapPlayerToPlayerDTO(newGame.getOpponentPlayer()));

        Game argumentGame = gameArgumentCaptor.getValue();

        Assert.assertNotNull(argumentGame.getOpponentPlayer());
        Assert.assertNotNull(argumentGame.getOwnerPlayer());

        Assert.assertEquals(argumentGame.getOwnerPlayer().getId(), newGame.getOwnerPlayer().getId());
        Assert.assertEquals(argumentGame.getOpponentPlayer().getId(), newGame.getOpponentPlayer().getId());
    }

    @Test(expected = NoSuchGameException.class)
    public void receiveSalvoWithNotActualGame() throws NoSuchGameException, NotYourTurnException {

        Game newGame = createSampleGame();
        newGame.getOwnerPlayer().setId(1);
        newGame.getOpponentPlayer().setId(2);

        Mockito.when(gameRepoService.getByGameId(newGame.getGameId())).thenReturn(null);

        gameService.receiveSalvo(new SalvoDTO(), newGame.getGameId());
        Mockito.verify(gameRepoService, Mockito.times(1)).getByGameId(newGame.getGameId());

    }

    @Test(expected = NotYourTurnException.class)
    public void receiveSalvoInYourTurn() throws NoSuchGameException, NotYourTurnException {

        Game newGame = createSampleGame();
        newGame.getOwnerPlayer().setId(1);
        newGame.getOpponentPlayer().setId(2);
        newGame.setStatus(GameStatus.ACTIVE);
        newGame.setPlayerInTurn(newGame.getOwnerPlayer());

        Mockito.when(gameRepoService.getByGameId(newGame.getGameId())).thenReturn(newGame);

        gameService.receiveSalvo(new SalvoDTO(), newGame.getGameId());

        Mockito.verify(gameRepoService, Mockito.times(1)).getByGameId(newGame.getGameId());

    }

    @Test
    public void playerInTurnHasToOwnerChangedAfterSalvoReceive() throws NoSuchGameException, NotYourTurnException {

        Game newGame = createSampleGame();
        newGame.getOwnerPlayer().setId(1);
        newGame.getOpponentPlayer().setId(2);
        newGame.setStatus(GameStatus.ACTIVE);
        newGame.setPlayerInTurn(newGame.getOpponentPlayer());

        SalvoDTO salvoDTO = new SalvoDTO();
        salvoDTO.setListOfShots(Stream.of("0x0").collect(Collectors.toList()));

        GameBoard gameBoard = new GameBoard();
        gameBoard.getFieldsCollection().stream().forEach(gameBoardPosition -> {
            gameBoardPosition.setGame(newGame);
            gameBoardPosition.setPlayer(newGame.getOwnerPlayer());
        });
        gameBoard.placeSpaceshipsOnTheBoard();

        Mockito.when(gameRepoService.getByGameId(newGame.getGameId())).thenReturn(newGame);
        Mockito.when(gameBoardRepoService.getOwnerGameBoardByGame(newGame.getGameId())).thenReturn(gameBoard.getFieldsCollection());

        SalvoResultDTO salvoResultDTO = gameService.receiveSalvo(salvoDTO, newGame.getGameId());

        Mockito.verify(gameRepoService, Mockito.times(1)).getByGameId(newGame.getGameId());

        Assert.assertNull(salvoResultDTO.getGameStatus().getWinningPlayer());
        Assert.assertNotNull(salvoResultDTO.getGameStatus().getPlayerInTurn());
        Assert.assertEquals(salvoResultDTO.getGameStatus().getPlayerInTurn(), newGame.getOwnerPlayer().getUserId());
    }

    @Test
    public void testSalvoHasBeenReflectedOnGameBoard() throws NoSuchGameException, NotYourTurnException {

        Game newGame = createSampleGame();
        newGame.getOwnerPlayer().setId(1);
        newGame.getOpponentPlayer().setId(2);
        newGame.setStatus(GameStatus.ACTIVE);
        newGame.setPlayerInTurn(newGame.getOpponentPlayer());

        SalvoDTO salvoDTO = new SalvoDTO();
        String hitShot = "2xB";
        salvoDTO.setListOfShots(Stream.of("0x0", hitShot, "AxF", "9x0", "2x3").collect(Collectors.toList()));

        GameBoard gameBoard = new GameBoard();
        gameBoard.getFieldsCollection().stream().forEach(gameBoardPosition -> {
            gameBoardPosition.setGame(newGame);
            gameBoardPosition.setPlayer(newGame.getOwnerPlayer());

            String[] rowsColumns = hitShot.split("x");

            if (gameBoardPosition.getColumn().equals(rowsColumns[1].toLowerCase().charAt(0)) && gameBoardPosition.getRow().equals(rowsColumns[0].toLowerCase().charAt(0))) {
                gameBoardPosition.setSpaceship(new Spaceship(SpaceshipType.ANGLE));
            }
        });

        gameBoard.placeSpaceshipsOnTheBoard();

        Mockito.when(gameRepoService.getByGameId(newGame.getGameId())).thenReturn(newGame);
        Mockito.when(gameBoardRepoService.getOwnerGameBoardByGame(newGame.getGameId())).thenReturn(gameBoard.getFieldsCollection());

        SalvoResultDTO salvoResultDTO = gameService.receiveSalvo(salvoDTO, newGame.getGameId());

        Mockito.verify(gameRepoService, Mockito.times(1)).getByGameId(newGame.getGameId());

        Assert.assertEquals(salvoResultDTO.getSalvoResult().get(hitShot), HitStatus.HIT);

    }


    private Game createSampleGame() {

        PlayerDTO opponentPlayer = new PlayerDTO();
        opponentPlayer.setUserId("xebialabs-1");
        opponentPlayer.setFullName("XebiaLabs Opponent");
        opponentPlayer.setSpaceshipProtocol(new SpaceshipProtocolDTO("127.0.0.1", 9001));

        PlayerDTO myPlayer = OwnerUtil.getSimulationUser();

        Game newGame = new Game();
        newGame.setOwnerPlayer(DTOMapperUtil.mapPlayerDTOToPlayer(myPlayer));
        newGame.setOpponentPlayer(DTOMapperUtil.mapPlayerDTOToPlayer(opponentPlayer));

        return newGame;
    }


}
