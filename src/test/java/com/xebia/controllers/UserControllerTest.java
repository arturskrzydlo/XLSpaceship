package com.xebia.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.config.TestConfiguration;
import com.xebia.dto.GameStatusDTO;
import com.xebia.dto.SalvoDTO;
import com.xebia.dto.SalvoResultDTO;
import com.xebia.enums.HitStatus;
import com.xebia.services.game.GameServiceClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by artur.skrzydlo on 2017-05-14.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfiguration.class)
public class UserControllerTest {

    @Mock
    private GameServiceClient gameServiceClient;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        initializeSalvo();
    }

    private SalvoResultDTO salvoResultDTO;

    private SalvoDTO salvoDTO;

    @Test
    public void testFireNoWinningSalvo() throws Exception {

        Integer gameId = 1;
        initializeSalvoResult(false, salvoDTO);

        Mockito.when(gameServiceClient.fireSalvo(Matchers.anyInt(), Matchers.any())).thenReturn(salvoResultDTO);

        mockMvc.perform(put("/xl-spaceship/user/game/" + gameId + "/fire")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(salvoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.game.player_turn").value(salvoResultDTO.getGameStatus().getPlayerInTurn()))
                .andExpect(jsonPath("$.game.won").doesNotExist())
                .andExpect(jsonPath("$.salvo.*", hasSize(salvoResultDTO.getSalvoResult().values().size())));

        ArgumentCaptor<SalvoDTO> dtoArgumentCaptor = ArgumentCaptor.forClass(SalvoDTO.class);
        verify(gameServiceClient, times(1)).fireSalvo(Matchers.anyInt(), dtoArgumentCaptor.capture());

        assertEquals(dtoArgumentCaptor.getValue().getListOfShots().size(), salvoDTO.getListOfShots().size());
        assertTrue(dtoArgumentCaptor.getValue().getListOfShots().containsAll(salvoDTO.getListOfShots()));


    }

    @Test
    public void testFireWinningSalvo() throws Exception {

        Integer gameId = 1;
        initializeSalvoResult(true, salvoDTO);

        //add one kill shoot - only that salvo can be a winning salvo
        salvoResultDTO.getSalvoResult().put("0x7", HitStatus.KILL);

        Mockito.when(gameServiceClient.fireSalvo(Matchers.anyInt(), Matchers.any())).thenReturn(salvoResultDTO);

        mockMvc.perform(put("/xl-spaceship/user/game/" + gameId + "/fire")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(salvoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.game.player_turn").doesNotExist())
                .andExpect(jsonPath("$.game.won").value(salvoResultDTO.getGameStatus().getWinningPlayer()))
                .andExpect(jsonPath("$.salvo").value(hasValue(HitStatus.KILL.name())))
                .andExpect(jsonPath("$.salvo.*", hasSize(salvoResultDTO.getSalvoResult().values().size())));

        ArgumentCaptor<SalvoDTO> dtoArgumentCaptor = ArgumentCaptor.forClass(SalvoDTO.class);
        verify(gameServiceClient, times(1)).fireSalvo(Matchers.anyInt(), dtoArgumentCaptor.capture());

        assertEquals(dtoArgumentCaptor.getValue().getListOfShots().size(), salvoDTO.getListOfShots().size());
        assertTrue(dtoArgumentCaptor.getValue().getListOfShots().containsAll(salvoDTO.getListOfShots()));

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


        GameStatusDTO gameStatusDTO = new GameStatusDTO();

        if (winningSalvo) {
            gameStatusDTO.setWinningPlayer("winning_player");
        } else {
            gameStatusDTO.setPlayerInTurn("testPlayer");
        }
        salvoResultDTO.setGameStatus(gameStatusDTO);
    }
}
