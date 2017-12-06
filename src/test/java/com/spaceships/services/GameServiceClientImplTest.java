package com.spaceships.services;

import com.spaceships.domains.Game;
import com.spaceships.domains.Player;
import com.spaceships.domains.Spaceship;
import com.spaceships.domains.SpaceshipProtocol;
import com.spaceships.dto.GameStatusDTO;
import com.spaceships.dto.SalvoDTO;
import com.spaceships.dto.SalvoResultDTO;
import com.spaceships.enums.GameStatus;
import com.spaceships.enums.PlayerType;
import com.spaceships.enums.SpaceshipType;
import com.spaceships.exceptions.GameHasFinishedException;
import com.spaceships.exceptions.IncorretSalvoShotsAmountException;
import com.spaceships.exceptions.NoSuchGameException;
import com.spaceships.services.game.GameService;
import com.spaceships.services.game.GameServiceClientImpl;
import com.spaceships.services.gameboard.GameBoard;
import com.spaceships.services.reposervices.game.GameRepoService;
import com.spaceships.services.reposervices.gameboard.GameBoardRepoService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by artur.skrzydlo on 2017-05-15.
 */
public class GameServiceClientImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private GameRepoService gameRepoService;

    @Mock
    private GameBoardRepoService gameBoardRepoService;

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameServiceClientImpl gameServiceClient;

    @Captor
    private ArgumentCaptor<HttpEntity<SalvoDTO>> salvoDTOCaptor;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = IncorretSalvoShotsAmountException.class)
    public void inCorrectNumberOfShotsInSalvo() throws Exception {

        Game actualGame = new Game();
        actualGame.setGameId("match-1");

        Mockito.when(gameRepoService.getByGameId(actualGame.getGameId())).thenReturn(actualGame);
        Mockito.when(gameBoardRepoService.getOwnerGameBoardByGame(actualGame.getGameId())).thenReturn(new GameBoard().getFieldsCollection());

        SalvoDTO salvoDTO = new SalvoDTO();
        salvoDTO.setListOfShots(Stream.of("0x1").collect(Collectors.toList()));
        gameServiceClient.fireSalvo(actualGame.getGameId(), salvoDTO);

        Mockito.verify(gameRepoService, Mockito.times(1)).getByGameId(actualGame.getGameId());
        Mockito.verify(gameBoardRepoService, Mockito.times(1)).getOwnerGameBoardByGame(actualGame.getGameId());
    }

    @Test
    public void correctEntityHasBeenSendViaPutReqest() throws NoSuchGameException, GameHasFinishedException {

        Game actualGame = new Game();
        actualGame.setId(1);
        actualGame.setGameId("match-1");

        SalvoDTO salvoDTO = new SalvoDTO();
        salvoDTO.setListOfShots(Stream.of("0x1").collect(Collectors.toList()));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        Player opponentPlayer = new Player();
        opponentPlayer.setUserId("opponent");
        opponentPlayer.setFullName("opponent");

        SpaceshipProtocol spaceshipProtocol = new SpaceshipProtocol();
        spaceshipProtocol.setHostname("127.0.0.1");
        spaceshipProtocol.setPort(9080);
        opponentPlayer.setProtocol(spaceshipProtocol);
        actualGame.setOpponentPlayer(opponentPlayer);

        Mockito.when(gameRepoService.getByGameId(actualGame.getGameId())).thenReturn(actualGame);
        GameBoard gameBoard = new GameBoard();
        gameBoard.getFieldsCollection().get(0).setSpaceship(new Spaceship(SpaceshipType.ANGLE));
        Mockito.when(gameBoardRepoService.getOwnerGameBoardByGame(actualGame.getGameId())).thenReturn(gameBoard.getFieldsCollection());
        ArgumentCaptor<HttpMethod> httpStatusArgumentCaptor = ArgumentCaptor.forClass(HttpMethod.class);

        Mockito.when(restTemplate.exchange(any(), any(), any(), eq(SalvoResultDTO.class), anyInt())).thenReturn(new ResponseEntity<>(new SalvoResultDTO(), HttpStatus.OK));
        gameServiceClient.fireSalvo(actualGame.getGameId(), salvoDTO);

        Mockito.verify(gameRepoService, Mockito.times(1)).getByGameId(actualGame.getGameId());
        Mockito.verify(gameBoardRepoService, Mockito.times(1)).getOwnerGameBoardByGame(actualGame.getGameId());

        verify(restTemplate, times(1)).exchange(any(), httpStatusArgumentCaptor.capture(), salvoDTOCaptor.capture(), eq(SalvoResultDTO.class), anyInt());


        assertNotNull(salvoDTOCaptor.getValue());
        assertNotNull(salvoDTOCaptor.getValue().getBody());
        assertEquals(salvoDTOCaptor.getValue().getBody().getListOfShots().size(), salvoDTO.getListOfShots().size());
        assertTrue(salvoDTOCaptor.getValue().getBody().getListOfShots().containsAll(salvoDTO.getListOfShots()));

        assertEquals(httpStatusArgumentCaptor.getValue(), HttpMethod.PUT);

    }

    @Test(expected = NoSuchGameException.class)
    public void getGameStatusOfNotExistingGame() throws NoSuchGameException {

        when(gameRepoService.getByGameId("match-1")).thenReturn(null);
        gameServiceClient.getGameStatus("match-1");

    }

    @Test
    public void gameStatusHasBeenCreated() throws NoSuchGameException {

        Game actualGame = new Game();
        actualGame.setId(1);
        actualGame.setStatus(GameStatus.ACTIVE);

        Player owner = new Player();
        owner.setId(1);
        owner.setUserId("player-1");
        owner.setPlayerType(PlayerType.OWNER);

        Player opponent = new Player();
        opponent.setId(2);
        opponent.setUserId("xebialabs-1");
        opponent.setPlayerType(PlayerType.OPPONENT);

        actualGame.setOpponentPlayer(opponent);
        actualGame.setOwnerPlayer(owner);
        actualGame.setPlayerInTurn(opponent);

        GameBoard ownerGameBoard = new GameBoard();
        ownerGameBoard.getFieldsCollection().stream().forEach(gameBoardPosition -> gameBoardPosition.setPlayer(owner));

        GameBoard opponentGameBoard = new GameBoard();
        opponentGameBoard.getFieldsCollection().stream().forEach(gameBoardPosition -> gameBoardPosition.setPlayer(opponent));

        when(gameRepoService.getByGameId(actualGame.getGameId())).thenReturn(actualGame);
        when(gameBoardRepoService.getOwnerGameBoardByGame(actualGame.getGameId())).thenReturn(ownerGameBoard.getFieldsCollection());
        when(gameBoardRepoService.getOpponentPlayerByGame(actualGame.getGameId(), opponent.getId())).thenReturn(opponentGameBoard.getFieldsCollection());

        GameStatusDTO gameStatusDTO = gameServiceClient.getGameStatus(actualGame.getGameId());

        verify(gameRepoService, times(1)).getByGameId(actualGame.getGameId());
        verify(gameBoardRepoService, times(1)).getOwnerGameBoardByGame(actualGame.getGameId());
        verify(gameBoardRepoService, times(1)).getOpponentPlayerByGame(actualGame.getGameId(), opponent.getId());

        assertTrue(gameStatusDTO.getOpponentGameBoard() != null);
        assertTrue(gameStatusDTO.getSelfGameBoard() != null);
        assertTrue(gameStatusDTO.getOpponentGameBoard().getGameBoardRows().size() == GameBoard.BOARD_SIZE);
        assertTrue(gameStatusDTO.getSelfGameBoard().getGameBoardRows().size() == GameBoard.BOARD_SIZE);
        assertEquals(gameStatusDTO.getSelfGameBoard().getUserId(), owner.getUserId());
        assertEquals(gameStatusDTO.getOpponentGameBoard().getUserId(), opponent.getUserId());
        assertTrue(gameStatusDTO.getGamePropertiesDTO().getPlayerInTurn() != null);
        assertTrue(gameStatusDTO.getGamePropertiesDTO().getWinningPlayer() == null);

    }



}
