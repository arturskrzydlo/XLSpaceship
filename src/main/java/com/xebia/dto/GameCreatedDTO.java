package com.xebia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"opponentId", "fullName", "gameId", "starting"})
public class GameCreatedDTO {

    @JsonProperty("user_id")
    private String opponentId;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("game_id")
    private String gameId;
    @JsonProperty("starting")
    private String startingPlayerId;

}
