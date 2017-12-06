package com.spaceships.services.game;

import com.spaceships.domains.Game;
import com.spaceships.domains.GameBoardPosition;
import com.spaceships.domains.Player;
import com.spaceships.domains.SpaceshipProtocol;
import com.spaceships.dto.*;
import com.spaceships.enums.GameStatus;
import com.spaceships.enums.PlayerType;
import com.spaceships.exceptions.GameHasFinishedException;
import com.spaceships.exceptions.IncorretSalvoShotsAmountException;
import com.spaceships.exceptions.NoSuchGameException;
import com.spaceships.services.gameboard.GameBoard;
import com.spaceships.services.gameboard.GameBoardService;
import com.spaceships.services.reposervices.game.GameRepoService;
import com.spaceships.services.reposervices.gameboard.GameBoardRepoService;
import com.spaceships.util.DTOMapperUtil;
import com.spaceships.util.OwnerUtil;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String FIRE_SALVO_RESOURCE = "/xl-spaceship/protocol/game";

    private static final String CHALLENGE_PLAYER = "/xl-spaceship/protocol/game/new";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GameService gameService;

    @Autowired
    private GameRepoService gameRepoService;

    @Autowired
    private GameBoardService gameBoardService;

    @Autowired
    private GameBoardRepoService gameBoardRepoService;

    @Override
    public SalvoResultDTO fireSalvo(String gameId, SalvoDTO salvo) throws NoSuchGameException, GameHasFinishedException {

        Game actualGame = gameRepoService.getByGameId(gameId);
        validateGame(salvo, actualGame);
        PlayerDTO opponent = DTOMapperUtil.mapPlayerToPlayerDTO(actualGame.getOpponentPlayer());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SalvoDTO> salvoDTOHttpEntity = new HttpEntity<>(salvo, httpHeaders);
        ResponseEntity<SalvoResultDTO> responseEntity = restTemplate.exchange(buildRequestStringFromOpponentPlayer(opponent, FIRE_SALVO_RESOURCE + "/" + actualGame.getGameId()), HttpMethod.PUT, salvoDTOHttpEntity, SalvoResultDTO.class, gameId);


        SalvoResultDTO salvoResultDTO = responseEntity.getBody();
        gameService.updateGameAfterYourSalvo(salvoResultDTO, gameId);
        return salvoResultDTO;
    }



    @Override
    public GameStatusDTO getGameStatus(String gameId) throws NoSuchGameException {

        Game actualGame = gameRepoService.getByGameId(gameId);
        validateGameExistence(actualGame);
        return createGameStatus(actualGame);
    }

    @Override
    public GameCreatedDTO challengePlayerForAGame(PlayerDTO playerToChallenge) {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PlayerDTO> playerDTOHttpEntity = new HttpEntity<>(OwnerUtil.getSimulationUser(), httpHeaders);
        ResponseEntity<GameCreatedDTO> responseEntity = restTemplate.postForEntity(buildRequestStringFromOpponentPlayer(playerToChallenge, CHALLENGE_PLAYER), playerDTOHttpEntity, GameCreatedDTO.class);

        GameCreatedDTO gameCreatedDTO = responseEntity.getBody();
        Game newGame = createNewGame(gameCreatedDTO, playerToChallenge);
        gameBoardRepoService.batchSave(createGameBoardForOwnerPlayer(newGame).getFieldsCollection());
        gameBoardRepoService.batchSave(createGameBoardForOpponentPlayer(newGame).getFieldsCollection());
        return gameCreatedDTO;
    }

    @Override
    public void turnOnAutopilot(String gameId) {
        Game actualGame = gameRepoService.getByGameId(gameId);
        actualGame.getOwnerPlayer().setAutopilot(true);
        gameRepoService.saveOrUpdate(actualGame);
    }

    @Override
    public List<GameDTO> getAllGames() {
        List<Game> allGames = (List<Game>) gameRepoService.listAll();
        return allGames.stream().map(game -> DTOMapperUtil.mapGameToGameDTO(game)).collect(Collectors.toList());
    }

    @Override
    public PlayerDTO getOwnerPlayerData() {
        return OwnerUtil.getSimulationUser();
    }

    private GameBoard createGameBoardForOwnerPlayer(Game newGame) {

        GameBoard gameBoard = gameBoardService.createGameBoard();
        gameBoard.getFieldsCollection().stream().forEach(gameBoardPosition -> {
            gameBoardPosition.setPlayer(newGame.getOwnerPlayer());
            gameBoardPosition.setGame(newGame);
        });

        return gameBoard;
    }

    private GameBoard createGameBoardForOpponentPlayer(Game newGame) {

        GameBoard gameBoard = gameBoardService.createEmptyGameBoard();
        gameBoard.getFieldsCollection().stream().forEach(gameBoardPosition -> {
            gameBoardPosition.setPlayer(newGame.getOpponentPlayer());
            gameBoardPosition.setGame(newGame);
        });

        return gameBoard;
    }

    private Game createNewGame(GameCreatedDTO gameCreatedDTO, PlayerDTO playerToChallenge) {

        Game newGame = new Game();
        newGame.setOwnerPlayer(DTOMapperUtil.mapPlayerDTOToPlayer(OwnerUtil.getSimulationUser()));
        newGame.getOwnerPlayer().setPlayerType(PlayerType.OWNER);
        Player opponent = new Player();
        opponent.setUserId(gameCreatedDTO.getOpponentId());
        opponent.setFullName(gameCreatedDTO.getFullName());
        opponent.setPlayerType(PlayerType.OPPONENT);

        SpaceshipProtocol spaceshipProtocol = new SpaceshipProtocol();
        spaceshipProtocol.setPort(playerToChallenge.getSpaceshipProtocol().getPort());
        spaceshipProtocol.setHostname(playerToChallenge.getSpaceshipProtocol().getHostname());
        opponent.setProtocol(spaceshipProtocol);
        newGame.setOpponentPlayer(opponent);

        if (gameCreatedDTO.getStartingPlayerId().equals(newGame.getOwnerPlayer().getUserId())) {
            newGame.setPlayerInTurn(newGame.getOwnerPlayer());
        } else {
            newGame.setPlayerInTurn(opponent);
        }

        newGame.setGameId(gameCreatedDTO.getGameId());
        gameRepoService.saveOrUpdate(newGame);

        return newGame;
    }

    private String buildRequestStringFromOpponentPlayer(PlayerDTO playerToChallenge, String requestURI) {

        SpaceshipProtocolDTO protocolToContact = playerToChallenge.getSpaceshipProtocol();
        return "http://" + protocolToContact.getHostname() + ":" + protocolToContact.getPort() + requestURI;

    }


    private GameStatusDTO createGameStatus(Game actualGame) {
        List<GameBoardPosition> ownerGameBoard = gameBoardRepoService.getOwnerGameBoardByGame(actualGame.getGameId());
        List<GameBoardPosition> opponentGameBoard = gameBoardRepoService.getOpponentPlayerByGame(actualGame.getGameId(), actualGame.getOpponentPlayer().getId());

        GameBoardDTO ownerGameBoardDTO = createGameBoardDTO(ownerGameBoard);
        GameBoardDTO opponentGameBoardDTO = createGameBoardDTO(opponentGameBoard);

        GameStatusDTO gameStatusDTO = new GameStatusDTO();
        gameStatusDTO.setOpponentGameBoard(opponentGameBoardDTO);
        gameStatusDTO.setSelfGameBoard(ownerGameBoardDTO);
        GamePropertiesDTO gamePropertiesDTO = new GamePropertiesDTO();

        if (actualGame.getWinningPlayer() != null) {
            gamePropertiesDTO.setWinningPlayer(actualGame.getWinningPlayer().getUserId());
        } else {
            gamePropertiesDTO.setPlayerInTurn(actualGame.getPlayerInTurn().getUserId());
        }

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
    }

    private void validateNumberOfShots(SalvoDTO salvoDTO, Game game) {

        List<GameBoardPosition> opponentGameBoard = gameBoardRepoService.getOwnerGameBoardByGame(game.getGameId());
        long numberOfAliveSpaceships = opponentGameBoard.stream()
                .distinct()
                .map(gameBoardPosition -> gameBoardPosition.getSpaceship())
                .filter(spaceship -> spaceship != null && spaceship.isAlive())
                .collect(Collectors.toSet()).size();

        if (numberOfAliveSpaceships != salvoDTO.getListOfShots().size()) {
            throw new IncorretSalvoShotsAmountException(salvoDTO.getListOfShots().size(), new Long(numberOfAliveSpaceships).intValue());
        }
    }


    private void validateGameStatus(Game actualGame) throws GameHasFinishedException {
        if (actualGame.getStatus().equals(GameStatus.FINISHED)) {
            throw new GameHasFinishedException(actualGame.getWinningPlayer().getUserId());
        }
    }


    private void validateGame(SalvoDTO salvo, Game actualGame) throws NoSuchGameException, GameHasFinishedException {

        if (actualGame == null) {
            throw new NoSuchGameException();
        }
        validateGameStatus(actualGame);
        validateNumberOfShots(salvo, actualGame);
    }
}
