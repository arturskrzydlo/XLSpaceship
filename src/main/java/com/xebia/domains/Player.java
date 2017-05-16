package com.xebia.domains;

import com.xebia.enums.PlayerType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

/**
 * Created by artur.skrzydlo on 2017-05-10.
 */
@Entity
@Getter
@Setter
public class Player extends AbstractDomainClass {

    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false)
    private PlayerType playerType;

    @Embedded
    private SpaceshipProtocol protocol;


}
