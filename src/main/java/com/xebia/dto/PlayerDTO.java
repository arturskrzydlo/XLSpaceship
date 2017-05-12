package com.xebia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class PlayerDTO {

    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("spaceship_protocol")
    private SpaceshipProtocolDTO spaceshipProtocol;
}
