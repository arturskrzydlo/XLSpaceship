package com.xebia.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.config.TestConfiguration;
import com.xebia.dto.*;
import com.xebia.enums.HitStatus;
import com.xebia.exceptions.IncorretSalvoShotsAmountException;
import com.xebia.exceptions.NoSuchGameException;
import com.xebia.exceptions.NotYourTurnException;
import com.xebia.exceptions.ShotOutOfBoardException;
import com.xebia.services.game.GameService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by artur.skrzydlo on 2017-05-13.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
public class ProtocolControllerTest {

    @Mock
    private GameService gameService;

    @InjectMocks
    private ProtocolController protocolController;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private SalvoResultDTO salvoResultDTO;

    private SalvoDTO salvoDTO;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(protocolController).build();
    }

    @Test
    public void testCreateNewGame() throws Exception {

        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setUserId("xebialabs-1");
        playerDTO.setFullName("XebiaLabs Opponent");
        playerDTO.setSpaceshipProtocol(new SpaceshipProtocolDTO("127.0.0.1", 9001));

        GameCreatedDTO gameCreatedDTO = new GameCreatedDTO();
        gameCreatedDTO.setStartingPlayerId(playerDTO.getUserId());
        gameCreatedDTO.setFullName("artur");
        gameCreatedDTO.setOpponentId("artur");
        gameCreatedDTO.setGameId(1);

        Mockito.when(gameService.createNewGame(Matchers.any())).thenReturn(gameCreatedDTO);

        mockMvc.perform(post("/xl-spaceship/protocol/game/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(playerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("user_id").value(gameCreatedDTO.getOpponentId()))
                .andExpect(jsonPath("full_name").value(gameCreatedDTO.getFullName()))
                .andExpect(jsonPath("full_name").value(gameCreatedDTO.getFullName()))
                .andExpect(jsonPath("game_id").value(gameCreatedDTO.getGameId()))
                .andExpect(jsonPath("starting").value(gameCreatedDTO.getStartingPlayerId()));


        ArgumentCaptor<PlayerDTO> dtoArgumentCaptor = ArgumentCaptor.forClass(PlayerDTO.class);
        verify(gameService, times(1)).createNewGame(dtoArgumentCaptor.capture());

        assertEquals(dtoArgumentCaptor.getValue().getFullName(), playerDTO.getFullName());
        assertNotNull(dtoArgumentCaptor.getValue().getSpaceshipProtocol());
        assertEquals(dtoArgumentCaptor.getValue().getSpaceshipProtocol().getHostname(), playerDTO.getSpaceshipProtocol().getHostname());
        assertEquals(dtoArgumentCaptor.getValue().getSpaceshipProtocol().getPort(), playerDTO.getSpaceshipProtocol().getPort());
        assertEquals(dtoArgumentCaptor.getValue().getUserId(), playerDTO.getUserId());

    }

    @Test
    public void testReceiveSalvo() throws Exception {

        Integer gameId = 1;
        initializeSalvo();
        initializeSalvoResult(false, salvoDTO);

        Mockito.when(gameService.receiveSalvo(Matchers.any(SalvoDTO.class), Matchers.anyInt())).thenReturn(salvoResultDTO);

        mockMvc.perform(put("/xl-spaceship/protocol/game/" + gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(salvoDTO)))
                .andExpect(status().isOk());

        ArgumentCaptor<SalvoDTO> dtoArgumentCaptor = ArgumentCaptor.forClass(SalvoDTO.class);
        verify(gameService, times(1)).receiveSalvo(dtoArgumentCaptor.capture(), Matchers.anyInt());

        assertEquals(dtoArgumentCaptor.getValue().getListOfShots().size(), salvoDTO.getListOfShots().size());
        assertTrue(dtoArgumentCaptor.getValue().getListOfShots().containsAll(salvoDTO.getListOfShots()));
    }

    @Test
    public void testReceiveSalvoWithIncorrectNumberOfShots() throws Exception {
        Integer gameId = 1;
        initializeSalvo();

        IncorretSalvoShotsAmountException incorretSalvoShotsAmountException = new IncorretSalvoShotsAmountException(5, 2);
        Mockito.when(gameService.receiveSalvo(Matchers.any(SalvoDTO.class), Matchers.anyInt())).thenThrow(incorretSalvoShotsAmountException);

        mockMvc.perform(put("/xl-spaceship/protocol/game/" + gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(salvoDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                .andExpect(jsonPath("$.message").value(incorretSalvoShotsAmountException.getMessage()));
    }


    @Test
    public void testReceiveSalvoWithShotOutsideGameBoard() throws Exception {
        Integer gameId = 1;
        initializeSalvo();

        ShotOutOfBoardException shotOutOfBoardException = new ShotOutOfBoardException("-1x0");
        Mockito.when(gameService.receiveSalvo(Matchers.any(SalvoDTO.class), Matchers.anyInt())).thenThrow(shotOutOfBoardException);

        mockMvc.perform(put("/xl-spaceship/protocol/game/" + gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(salvoDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                .andExpect(jsonPath("$.message").value(shotOutOfBoardException.getMessage()));
    }

    @Test
    public void testReceiveSalvoInYourTurn() throws Exception {
        Integer gameId = 1;
        initializeSalvo();

        NotYourTurnException notYourTurnException = new NotYourTurnException();
        Mockito.when(gameService.receiveSalvo(Matchers.any(SalvoDTO.class), Matchers.anyInt())).thenThrow(notYourTurnException);

        mockMvc.perform(put("/xl-spaceship/protocol/game/" + gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(salvoDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                .andExpect(jsonPath("$.message").value(notYourTurnException.getMessage()));
    }

    @Test
    public void testReceiveSalvoWithWrongGameId() throws Exception {
        Integer gameId = 1;
        initializeSalvo();

        NoSuchGameException noSuchGameException = new NoSuchGameException(gameId);
        Mockito.when(gameService.receiveSalvo(Matchers.any(SalvoDTO.class), Matchers.anyInt())).thenThrow(noSuchGameException);

        mockMvc.perform(put("/xl-spaceship/protocol/game/" + gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(salvoDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value(HttpStatus.UNPROCESSABLE_ENTITY.value()))
                .andExpect(jsonPath("$.message").value(noSuchGameException.getMessage()));
    }

    private void initializeSalvo() {
        salvoDTO = new SalvoDTO();
        salvoDTO.setListOfShots(new ArrayList<>());
        salvoDTO.getListOfShots().add("0x0");
        salvoDTO.getListOfShots().add("Bx7");
        salvoDTO.getListOfShots().add("Bx0");
        salvoDTO.getListOfShots().add("AxF");
        salvoDTO.getListOfShots().add("9x2");
    }

    private void initializeSalvoResult(boolean winningSalvo, SalvoDTO salvoDTO) {

        salvoResultDTO = new SalvoResultDTO();
        salvoResultDTO.setSalvoResult(new HashMap<>());
        salvoDTO.getListOfShots().stream().forEach(salvo -> {
            Random random = new Random();
            salvoResultDTO.getSalvoResult().put(salvo, random.nextBoolean() ? HitStatus.HIT : HitStatus.MISS);
        });


        GamePropertiesDTO gamePropertiesDTO = new GamePropertiesDTO();

        if (winningSalvo) {
            gamePropertiesDTO.setWinningPlayer("winning_player");
        } else {
            gamePropertiesDTO.setPlayerInTurn("testPlayer");
        }
        salvoResultDTO.setGameStatus(gamePropertiesDTO);
    }
}
