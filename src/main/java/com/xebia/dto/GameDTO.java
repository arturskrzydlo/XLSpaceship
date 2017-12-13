package com.xebia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Created by artur.skrzydlo on 2017-05-10.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GameDTO {


    private PlayerDTO opponent;
    @JsonProperty("game_id")
    private String gameId;
    @JsonProperty("starting")
    private PlayerDTO startingPlayer;
}
