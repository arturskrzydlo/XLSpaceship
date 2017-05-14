package com.xebia.services;

import com.xebia.domains.Game;
import com.xebia.dto.GameCreatedDTO;
import com.xebia.dto.PlayerDTO;
import com.xebia.dto.SpaceshipProtocolDTO;
import com.xebia.enums.GameStatus;
import com.xebia.services.gameboard.GameBoard;
import com.xebia.services.gameboard.GameBoardService;
import com.xebia.util.DTOMapperUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by artur.skrzydlo on 2017-05-13.
 */
//TODO:more tests !
@RunWith(SpringRunner.class)
public class GameServiceImplTest {


    @Mock
    private GameRepoService gameRepoService;

    @Mock
    private GameBoardService gameBoardService;

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

        Mockito.when(gameBoardService.createGameBoard()).thenReturn(new GameBoard());
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


}
