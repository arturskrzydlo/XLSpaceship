package com.spaceships.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.spaceships.enums.HitStatus;
import lombok.*;

import java.util.Map;

/**
 * Created by artur.skrzydlo on 2017-05-13.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonRootName("salvo")
@ToString
public class SalvoResultDTO {

    @JsonProperty("salvo")
    private Map<String, HitStatus> salvoResult;
    @JsonProperty("game")
    private GamePropertiesDTO gameStatus;

    @JsonAnySetter
    public void add(String key, HitStatus value) {

        salvoResult.put(key, value);
    }
}
