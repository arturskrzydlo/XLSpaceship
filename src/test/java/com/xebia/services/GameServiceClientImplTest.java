package com.xebia.services;

import com.xebia.domains.Game;
import com.xebia.domains.Spaceship;
import com.xebia.dto.SalvoDTO;
import com.xebia.dto.SalvoResultDTO;
import com.xebia.enums.SpaceshipType;
import com.xebia.exceptions.IncorretSalvoShotsAmountException;
import com.xebia.services.game.GameService;
import com.xebia.services.game.GameServiceClientImpl;
import com.xebia.services.gameboard.GameBoard;
import com.xebia.services.reposervices.game.GameRepoService;
import com.xebia.services.reposervices.gameboard.GameBoardRepoService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
        actualGame.setId(1);

        Mockito.when(gameRepoService.getById(actualGame.getId())).thenReturn(actualGame);
        Mockito.when(gameBoardRepoService.getOwnerGameBoardByGame(actualGame.getId())).thenReturn(new GameBoard().getFieldsCollection());

        SalvoDTO salvoDTO = new SalvoDTO();
        salvoDTO.setListOfShots(Stream.of("0x1").collect(Collectors.toList()));
        gameServiceClient.fireSalvo(actualGame.getId(), salvoDTO);

        Mockito.verify(gameRepoService, Mockito.times(1)).getById(actualGame.getId());
        Mockito.verify(gameBoardRepoService, Mockito.times(1)).getOwnerGameBoardByGame(actualGame.getId());
    }

    @Test
    public void correctEntityHasBeenSendViaPutReqest() {

        Game actualGame = new Game();
        actualGame.setId(1);

        SalvoDTO salvoDTO = new SalvoDTO();
        salvoDTO.setListOfShots(Stream.of("0x1").collect(Collectors.toList()));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        Mockito.when(gameRepoService.getById(actualGame.getId())).thenReturn(actualGame);
        GameBoard gameBoard = new GameBoard();
        gameBoard.getFieldsCollection().get(0).setSpaceship(new Spaceship(SpaceshipType.SCLASS));
        Mockito.when(gameBoardRepoService.getOwnerGameBoardByGame(actualGame.getId())).thenReturn(gameBoard.getFieldsCollection());
        ArgumentCaptor<HttpMethod> httpStatusArgumentCaptor = ArgumentCaptor.forClass(HttpMethod.class);

        Mockito.when(restTemplate.exchange(any(), any(), any(), eq(SalvoResultDTO.class), anyInt())).thenReturn(new ResponseEntity<>(new SalvoResultDTO(), HttpStatus.OK));
        gameServiceClient.fireSalvo(actualGame.getId(), salvoDTO);

        Mockito.verify(gameRepoService, Mockito.times(1)).getById(actualGame.getId());
        Mockito.verify(gameBoardRepoService, Mockito.times(1)).getOwnerGameBoardByGame(actualGame.getId());

        verify(restTemplate, times(1)).exchange(any(), httpStatusArgumentCaptor.capture(), salvoDTOCaptor.capture(), eq(SalvoResultDTO.class), anyInt());


        assertNotNull(salvoDTOCaptor.getValue());
        assertNotNull(salvoDTOCaptor.getValue().getBody());
        assertEquals(salvoDTOCaptor.getValue().getBody().getListOfShots().size(), salvoDTO.getListOfShots().size());
        assertTrue(salvoDTOCaptor.getValue().getBody().getListOfShots().containsAll(salvoDTO.getListOfShots()));

        assertEquals(httpStatusArgumentCaptor.getValue(), HttpMethod.PUT);


    }


}
