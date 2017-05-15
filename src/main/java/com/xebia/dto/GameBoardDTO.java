package com.xebia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by artur.skrzydlo on 2017-05-15.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameBoardDTO {

    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("board")
    private List<String> gameBoardRows;
}
