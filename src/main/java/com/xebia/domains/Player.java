package com.xebia.domains;

import com.xebia.enums.PlayerType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embedded;
import javax.persistence.Entity;

/**
 * Created by artur.skrzydlo on 2017-05-10.
 */
@Entity
@Getter
@Setter
public class Player extends AbstractDomainClass {

    private String userId;
    private String fullName;
    private PlayerType playerType;

    @Embedded
    private SpaceshipProtocol protocol;


}
