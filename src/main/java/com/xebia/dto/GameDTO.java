package com.xebia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by artur.skrzydlo on 2017-05-10.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameDTO {


    private PlayerDTO opponent;
    @JsonProperty("game_id")
    private Integer gameId;
    @JsonProperty("starting")
    private PlayerDTO startingPlayer;
}
