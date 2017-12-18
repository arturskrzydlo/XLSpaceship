package com.xebia.thread;

import com.sun.istack.internal.Nullable;
import com.xebia.domains.GameBoardPosition;
import com.xebia.dto.PlayerDTO;
import com.xebia.dto.SalvoDTO;
import com.xebia.dto.SalvoResultDTO;
import com.xebia.dto.SpaceshipProtocolDTO;
import com.xebia.enums.HitStatus;
import com.xebia.exceptions.NotYourTurnException;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Created by artur.skrzydlo on 2017-05-16.
 */
@Component
public class AutoFireResponse {

    private String fireSalvoResource = "/xl-spaceship/user/game/{0}/fire";
    private String gameId;
    private PlayerDTO opponent;
    private List<GameBoardPosition> opponentPlayerGameBoard;
    private List<GameBoardPosition> ownerPlayerGameboard;
    private RestTemplate restTemplate;
    private SalvoResultDTO salvoResultDTO;

    public AutoFireResponse(String gameId, PlayerDTO opponent) {
        this.gameId = gameId;
        this.opponent = opponent;
        fireSalvoResource = MessageFormat.format(fireSalvoResource, gameId.toString());
    }

    public AutoFireResponse() {
    }

    private static Object processGeneralException(Throwable throwable) {
        throw new RuntimeException(throwable);
    }


    public Optional<SalvoResultDTO> autoFireResponseOpponent() {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        SalvoDTO salvoDTO = createRandomSalvo();

        HttpEntity<SalvoDTO> salvoDTOHttpEntity = new HttpEntity<>(salvoDTO, httpHeaders);
        ResponseEntity<SalvoResultDTO> responseEntity = restTemplate.exchange(buildRequestStringFromOpponentPlayer(opponent, fireSalvoResource), HttpMethod.PUT, salvoDTOHttpEntity, SalvoResultDTO.class);
        SalvoResultDTO salvoResultDTO = responseEntity.getBody();
        return Optional.ofNullable(salvoResultDTO);

    }

    public void autoFire() {

        CompletableFuture<Optional<SalvoResultDTO>> futureResult = CompletableFuture.supplyAsync(this::executeAutoFireResponseOpponentAsync);
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        CompletableFuture<Object> result = CompletableFuture.anyOf(ThreadUtils.timeoutAfter(10, TimeUnit.SECONDS), futureResult);

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            System.out.println("Checking if job is done");
            if (result.isDone()) {
                System.out.println("JOB Done");
                result
                        .exceptionally(AutoFireResponse::processGeneralException)
                        .thenAccept(salvoResult -> salvoResultDTO = (SalvoResultDTO) salvoResult);
                scheduledExecutorService.shutdown();
            }

        }, 500, 500, TimeUnit.MILLISECONDS);
    }

    public Optional<SalvoResultDTO> executeAutoFireResponseOpponentAsync() {

        Optional<SalvoResultDTO> resultDTO = Optional.empty();
        CompletableFuture<Optional<SalvoResultDTO>> futureResult = CompletableFuture.supplyAsync(this::autoFireResponseOpponent);

        try {
            resultDTO = futureResult
                    .exceptionally(this::processCustomClientException)
                    .thenApply(result -> result).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        while (!resultDTO.isPresent()) {
            resultDTO = executeAutoFireResponseOpponentAsync();
        }

        return resultDTO;
    }

    @Nullable
    private Optional<SalvoResultDTO> processCustomClientException(Throwable exception) {

        if (exception.getMessage().contains(NotYourTurnException.MESSAGE)) {
            return Optional.empty();
        } else {
            throw new RuntimeException(exception);
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

