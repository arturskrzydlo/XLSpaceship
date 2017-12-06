package com.spaceships.thread;

import com.spaceships.domains.GameBoardPosition;
import com.spaceships.dto.PlayerDTO;
import com.spaceships.dto.SalvoDTO;
import com.spaceships.dto.SalvoResultDTO;
import com.spaceships.dto.SpaceshipProtocolDTO;
import com.spaceships.enums.HitStatus;
import com.spaceships.exceptions.CustomClientException;
import com.spaceships.exceptions.NotYourTurnException;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Created by artur.skrzydlo on 2017-05-16.
 */
@Component
@Scope("prototype")
public class AutoFireResponseThread extends Thread {

    private String fireSalvoResource = "/xl-spaceship/user/game/{0}/fire";
    private String gameId;
    private PlayerDTO opponent;
    private List<GameBoardPosition> opponentPlayerGameBoard;
    private List<GameBoardPosition> ownerPlayerGameboard;
    private RestTemplate restTemplate;

    public AutoFireResponseThread(String gameId, PlayerDTO opponent) {
        this.gameId = gameId;
        this.opponent = opponent;
        fireSalvoResource = MessageFormat.format(fireSalvoResource, gameId.toString());
    }

    public AutoFireResponseThread() {
    }

    @Override
    public void run() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        SalvoDTO salvoDTO = createRandomSalvo();

        try {
            HttpEntity<SalvoDTO> salvoDTOHttpEntity = new HttpEntity<>(salvoDTO, httpHeaders);
            ResponseEntity<SalvoResultDTO> responseEntity = restTemplate.exchange(buildRequestStringFromOpponentPlayer(opponent, fireSalvoResource), HttpMethod.PUT, salvoDTOHttpEntity, SalvoResultDTO.class);
            SalvoResultDTO salvoResultDTO = responseEntity.getBody();
        } catch (CustomClientException exc) {

            if (exc.getBody().contains(NotYourTurnException.MESSAGE)) {
                AutoFireResponseThread autoFireResponseThread = new AutoFireResponseThread();
                autoFireResponseThread.setGameId(gameId);
                autoFireResponseThread.setOpponent(getOpponent());
                autoFireResponseThread.setOpponentPlayerGameBoard(getOpponentPlayerGameBoard());
                autoFireResponseThread.setOwnerPlayerGameboard(getOwnerPlayerGameboard());
                autoFireResponseThread.setRestTemplate(getRestTemplate());
                autoFireResponseThread.start();
            }
        }
    }


    private String buildRequestStringFromOpponentPlayer(PlayerDTO myPlayer, String requestURI) {

        SpaceshipProtocolDTO protocolToContact = myPlayer.getSpaceshipProtocol();
        return "http://" + protocolToContact.getHostname() + ":" + protocolToContact.getPort() + requestURI;

    }

    private SalvoDTO createRandomSalvo() {

        List<String> salvoShots = new ArrayList<>();

        List<GameBoardPosition> notCheckedYetPositions = getOpponentPlayerGameBoard().stream().filter(gameBoardPosition -> {

            HitStatus hitStatus = gameBoardPosition.getHitStatus();
            if (hitStatus.equals(HitStatus.NOT_FIRED_YET)) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());

        for (int i = 0; i < howManyShipsAlive(getOwnerPlayerGameboard()); i++) {
            int index = ThreadLocalRandom.current().nextInt(0, notCheckedYetPositions.size());
            GameBoardPosition randomlyChoosenField = notCheckedYetPositions.get(index);
            salvoShots.add(randomlyChoosenField.getRow() + "x" + randomlyChoosenField.getColumn());
            notCheckedYetPositions.remove(index);
        }

        return new SalvoDTO(salvoShots);
    }

    private long howManyShipsAlive(List<GameBoardPosition> gameBoard) {

        return gameBoard.stream()
                .distinct()
                .map(gameBoardPosition -> gameBoardPosition.getSpaceship())
                .filter(spaceship -> spaceship != null && spaceship.isAlive())
                .collect(Collectors.toSet()).size();

    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        fireSalvoResource = MessageFormat.format(fireSalvoResource, gameId.toString());
        this.gameId = gameId;
    }

    public PlayerDTO getOpponent() {
        return opponent;
    }

    public void setOpponent(PlayerDTO opponent) {
        this.opponent = opponent;
    }

    public List<GameBoardPosition> getOpponentPlayerGameBoard() {
        return opponentPlayerGameBoard;
    }

    public void setOpponentPlayerGameBoard(List<GameBoardPosition> opponentPlayerGameBoard) {
        this.opponentPlayerGameBoard = opponentPlayerGameBoard;
    }

    public List<GameBoardPosition> getOwnerPlayerGameboard() {
        return ownerPlayerGameboard;
    }

    public void setOwnerPlayerGameboard(List<GameBoardPosition> ownerPlayerGameboard) {
        this.ownerPlayerGameboard = ownerPlayerGameboard;
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}

