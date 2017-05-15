package com.xebia.services.game;

import com.xebia.domains.Game;
import com.xebia.domains.GameBoardPosition;
import com.xebia.dto.*;
import com.xebia.enums.GameStatus;
import com.xebia.exceptions.IncorretSalvoShotsAmountException;
import com.xebia.exceptions.NoSuchGameException;
import com.xebia.services.gameboard.GameBoard;
import com.xebia.services.reposervices.game.GameRepoService;
import com.xebia.services.reposervices.gameboard.GameBoardRepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
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


    @Override
    public GameStatusDTO getGameStatus(Integer gameId) throws NoSuchGameException {

        Game actualGame = gameRepoService.getById(gameId);
        validateGameExistence(actualGame);
        return createGameStatus(actualGame);
    }

    private GameStatusDTO createGameStatus(Game actualGame) {
        List<GameBoardPosition> ownerGameBoard = gameBoardRepoService.getOwnerGameBoardByGame(actualGame.getId());
        List<GameBoardPosition> opponentGameBoard = gameBoardRepoService.getOpponentPlayerByGame(actualGame.getId(), actualGame.getOpponentPlayer().getId());

        GameBoardDTO ownerGameBoardDTO = createGameBoardDTO(ownerGameBoard);
        GameBoardDTO opponentGameBoardDTO = createGameBoardDTO(opponentGameBoard);

        GameStatusDTO gameStatusDTO = new GameStatusDTO();
        gameStatusDTO.setOpponentGameBoard(opponentGameBoardDTO);
        gameStatusDTO.setSelfGameBoard(ownerGameBoardDTO);
        GamePropertiesDTO gamePropertiesDTO = new GamePropertiesDTO();
        gamePropertiesDTO.setPlayerInTurn(actualGame.getPlayerInTurn().getUserId());

        gameStatusDTO.setGamePropertiesDTO(gamePropertiesDTO);
        return gameStatusDTO;
    }

    private GameBoardDTO createGameBoardDTO(List<GameBoardPosition> opponentGameBoard) {

        List<String> gameBoardRows = new ArrayList<>();
        StringBuilder gameBoardRowPresentation = new StringBuilder();

        for (int index = 0; index < opponentGameBoard.size(); index++) {

            GameBoardPosition position = opponentGameBoard.get(index);
            switch (position.getHitStatus()) {

                case HIT:
                case KILL:
                    gameBoardRowPresentation.append("X");
                    break;
                case MISS:
                    gameBoardRowPresentation.append("-");
                    break;
                case NOT_FIRED_YET:
                    if (position.getSpaceship() != null) {
                        gameBoardRowPresentation.append("*");
                    } else {
                        gameBoardRowPresentation.append(".");
                    }
                    break;
                default:
            }

            if ((index + 1) % GameBoard.BOARD_SIZE == 0) {
                gameBoardRows.add(gameBoardRowPresentation.toString());
                gameBoardRowPresentation = new StringBuilder();
            }
        }

        String userId = opponentGameBoard.get(0).getPlayer().getUserId();
        GameBoardDTO gameBoardDTO = new GameBoardDTO(userId, gameBoardRows);
        return gameBoardDTO;
    }

    private void validateGameExistence(Game actualGame) throws NoSuchGameException {
        if (actualGame == null) {
            throw new NoSuchGameException();
        }

        if (actualGame.getStatus().equals(GameStatus.FINISHED)) {
            throw new NoSuchGameException(actualGame.getId());
        }
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
