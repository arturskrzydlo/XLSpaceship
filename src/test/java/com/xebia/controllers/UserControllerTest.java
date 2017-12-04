package com.xebia.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.dto.*;
import com.xebia.enums.HitStatus;
import com.xebia.services.game.GameServiceClient;
import com.xebia.services.gameboard.GameBoard;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by artur.skrzydlo on 2017-05-14.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    private GameServiceClient gameServiceClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Before
    public void setup() {
        initializeSalvo();
    }

    private SalvoResultDTO salvoResultDTO;

    private SalvoDTO salvoDTO;

    @Test
    public void testFireNoWinningSalvo() throws Exception {

        String gameId = "match-1";
        initializeSalvoResult(false, salvoDTO);

        Mockito.when(gameServiceClient.fireSalvo(Matchers.anyString(), Matchers.any())).thenReturn(salvoResultDTO);

        mockMvc.perform(put("/xl-spaceship/user/game/" + gameId + "/fire")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(salvoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.game.player_turn").value(salvoResultDTO.getGameStatus().getPlayerInTurn()))
                .andExpect(jsonPath("$.game.won").doesNotExist())
                .andExpect(jsonPath("$.salvo.*", hasSize(salvoResultDTO.getSalvoResult().values().size())));

        ArgumentCaptor<SalvoDTO> dtoArgumentCaptor = ArgumentCaptor.forClass(SalvoDTO.class);
        verify(gameServiceClient, times(1)).fireSalvo(Matchers.anyString(), dtoArgumentCaptor.capture());

        assertEquals(dtoArgumentCaptor.getValue().getListOfShots().size(), salvoDTO.getListOfShots().size());
        assertTrue(dtoArgumentCaptor.getValue().getListOfShots().containsAll(salvoDTO.getListOfShots()));


    }

    @Test
    public void testFireWinningSalvo() throws Exception {

        String gameId = "match-1";
        initializeSalvoResult(true, salvoDTO);

        //add one kill shoot - only that salvo can be a winning salvo
        salvoResultDTO.getSalvoResult().put("0x7", HitStatus.KILL);

        Mockito.when(gameServiceClient.fireSalvo(Matchers.anyString(), Matchers.any())).thenReturn(salvoResultDTO);

        mockMvc.perform(put("/xl-spaceship/user/game/" + gameId + "/fire")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(salvoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.game.player_turn").doesNotExist())
                .andExpect(jsonPath("$.game.won").value(salvoResultDTO.getGameStatus().getWinningPlayer()))
                .andExpect(jsonPath("$.salvo").value(hasValue(HitStatus.KILL.name())))
                .andExpect(jsonPath("$.salvo.*", hasSize(salvoResultDTO.getSalvoResult().values().size())));

        ArgumentCaptor<SalvoDTO> dtoArgumentCaptor = ArgumentCaptor.forClass(SalvoDTO.class);
        verify(gameServiceClient, times(1)).fireSalvo(Matchers.anyString(), dtoArgumentCaptor.capture());

        assertEquals(dtoArgumentCaptor.getValue().getListOfShots().size(), salvoDTO.getListOfShots().size());
        assertTrue(dtoArgumentCaptor.getValue().getListOfShots().containsAll(salvoDTO.getListOfShots()));

    }

    @Test
    public void testGetGameStatus() throws Exception {

        String gameId = "match-1";
        GameStatusDTO gameStatusDTO = createGameStatusDTO();

        when(gameServiceClient.getGameStatus(gameId)).thenReturn(gameStatusDTO);

        MvcResult result = mockMvc.perform(get("/xl-spaceship/user/game/" + gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*").value(hasSize(3)))
                .andExpect(jsonPath("$.self.user_id").value(gameStatusDTO.getSelfGameBoard().getUserId()))
                .andExpect(jsonPath("$.self.board.*").value(hasSize(GameBoard.BOARD_SIZE)))
                .andExpect(jsonPath("$.opponent.user_id").value(gameStatusDTO.getOpponentGameBoard().getUserId()))
                .andExpect(jsonPath("$.opponent.board.*").value(hasSize(GameBoard.BOARD_SIZE)))
                .andExpect(jsonPath("$.game.player_turn").exists())
                .andReturn();

        result.getResponse().getContentAsString();

        verify(gameServiceClient, times(1)).getGameStatus(gameId);
    }

    private GameStatusDTO createGameStatusDTO() {

        String opponentId = "xebialabs-1";
        String ownerId = "player-1";

        GameStatusDTO gameStatusDTO = new GameStatusDTO();
        GamePropertiesDTO gamePropertiesDTO = new GamePropertiesDTO();
        gamePropertiesDTO.setPlayerInTurn(ownerId);
        gameStatusDTO.setGamePropertiesDTO(new GamePropertiesDTO());

        GameBoardDTO ownerGameBoard = new GameBoardDTO();
        GameBoardDTO opponentGameBoard = new GameBoardDTO();
        List<String> gameBoardRows = new ArrayList<>();
        StringBuilder gameBoardBuilder = new StringBuilder();

        for (int index = 0; index < GameBoard.BOARD_SIZE * GameBoard.BOARD_SIZE; index++) {
            gameBoardBuilder.append(".");
            if (((index + 1) % GameBoard.BOARD_SIZE) == 0) {
                gameBoardRows.add(gameBoardBuilder.toString());
                gameBoardBuilder = new StringBuilder();
            }
        }

        ownerGameBoard.setGameBoardRows(gameBoardRows);
        ownerGameBoard.setUserId(ownerId);
        opponentGameBoard.setGameBoardRows(gameBoardRows);
        opponentGameBoard.setUserId(opponentId);

        gameStatusDTO.setOpponentGameBoard(opponentGameBoard);
        gameStatusDTO.setSelfGameBoard(ownerGameBoard);
        gameStatusDTO.setGamePropertiesDTO(gamePropertiesDTO);

        return gameStatusDTO;
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
