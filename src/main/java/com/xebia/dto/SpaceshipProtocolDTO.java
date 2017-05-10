package com.xebia.dto;

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
public class SpaceshipProtocolDTO {

    private String hostname;
    private String port;
}
