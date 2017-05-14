package com.xebia.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.xebia.enums.ShotResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * Created by artur.skrzydlo on 2017-05-13.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonRootName("salvo")
public class SalvoResultDTO {

    @JsonProperty("salvo")
    private Map<ShotDTO, ShotResult> salvoResult;
    @JsonProperty("game")
    private GameStatusDTO gameStatus;

    @JsonAnySetter
    public void add(ShotDTO key, ShotResult value) {

        salvoResult.put(key, value);
    }
}
