package com.xebia.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by artur.skrzydlo on 2017-05-13.
 */
@Getter
@Setter
@NoArgsConstructor
public class ShotDTO {

    private String field;

    public ShotDTO(String field) {
        this.field = field;
    }
}
