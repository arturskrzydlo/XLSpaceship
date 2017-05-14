package com.xebia.services;

import com.xebia.domains.Game;
import com.xebia.domains.GameBoardPosition;
import com.xebia.dto.GameCreatedDTO;
import com.xebia.dto.PlayerDTO;
import com.xebia.dto.SpaceshipProtocolDTO;
import com.xebia.enums.GameStatus;
import com.xebia.services.gameboard.GameBoard;
import com.xebia.services.gameboard.GameBoardService;
import com.xebia.util.DTOMapperUtil;
import com.xebia.util.OwnerUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

/**
 * Created by artur.skrzydlo on 2017-05-13.
 */
//TODO:more tests !
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
    public void newGameHasBeenCreated() {

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
    public void playersHasBeanCreatedIfTheyDontExists() {

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
    public void playersHasBeanRetrievedIfTheyExists() {

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
