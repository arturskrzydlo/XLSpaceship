package com.xebia.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xebia.config.TestConfiguration;
import com.xebia.dto.GameCreatedDTO;
import com.xebia.dto.PlayerDTO;
import com.xebia.dto.SpaceshipProtocolDTO;
import com.xebia.services.GameService;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
