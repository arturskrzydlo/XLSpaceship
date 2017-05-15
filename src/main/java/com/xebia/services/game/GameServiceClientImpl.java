package com.xebia.services.game;

import com.xebia.domains.Game;
import com.xebia.domains.GameBoardPosition;
import com.xebia.dto.SalvoDTO;
import com.xebia.dto.SalvoResultDTO;
import com.xebia.exceptions.IncorretSalvoShotsAmountException;
import com.xebia.services.reposervices.game.GameRepoService;
import com.xebia.services.reposervices.gameboard.GameBoardRepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by artur.skrzydlo on 2017-05-14.
 */
@Service
public class GameServiceClientImpl implements GameServiceClient {

    @Value("${resource.game}")
    private String resource;

    @Value("${resource.game}/{gameId}")
    private String fireSalvoResource;


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GameService gameService;

    @Autowired
    private GameRepoService gameRepoService;

    @Autowired
    private GameBoardRepoService gameBoardRepoService;

    @Override
    public SalvoResultDTO fireSalvo(Integer gameId, SalvoDTO salvo) {

        Game actualGame = gameRepoService.getById(gameId);
        validateNumberOfShots(salvo, actualGame);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SalvoDTO> salvoDTOHttpEntity = new HttpEntity<>(salvo, httpHeaders);
        ResponseEntity<SalvoResultDTO> responseEntity = restTemplate.exchange(fireSalvoResource, HttpMethod.PUT, salvoDTOHttpEntity, SalvoResultDTO.class, gameId);

        SalvoResultDTO salvoResultDTO = responseEntity.getBody();
        gameService.updateGameWithSalvoResult(salvoResultDTO, gameId);
        return salvoResultDTO;
    }


    private void validateNumberOfShots(SalvoDTO salvoDTO, Game game) {

        List<GameBoardPosition> opponentGameBoard = gameBoardRepoService.getOwnerGameBoardByGame(game.getId());
        long numberOfAliveSpaceships = opponentGameBoard.stream()
                .distinct()
                .map(gameBoardPosition -> gameBoardPosition.getSpaceship())
                .filter(spaceship -> spaceship != null && spaceship.isAlive())
                .collect(Collectors.toSet()).size();

        if (numberOfAliveSpaceships != salvoDTO.getListOfShots().size()) {
            throw new IncorretSalvoShotsAmountException(salvoDTO.getListOfShots().size(), new Long(numberOfAliveSpaceships).intValue());
        }
    }

    public String getResource() {
        return resource;
    }

    public String getFireSalvoResource() {
        return fireSalvoResource;
    }
}
