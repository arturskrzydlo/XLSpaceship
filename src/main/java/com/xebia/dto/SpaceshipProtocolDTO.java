package com.xebia.dto;

import lombok.*;

/**
 * Created by artur.skrzydlo on 2017-05-10.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SpaceshipProtocolDTO {

    private String hostname;
    private Integer port;
}
