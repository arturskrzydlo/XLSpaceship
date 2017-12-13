package com.xebia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * Created by artur.skrzydlo on 2017-05-15.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GameBoardDTO {

    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("board")
    private List<String> gameBoardRows;
}
