package com.xebia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by artur.skrzydlo on 2017-05-15.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameStatusDTO {

    @JsonProperty("self")
    private GameBoardDTO selfGameBoard;
    @JsonProperty("opponent")
    private GameBoardDTO opponentGameBoard;
    @JsonProperty("game")
    private GamePropertiesDTO gamePropertiesDTO;

}
