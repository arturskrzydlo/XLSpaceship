package com.xebia.domains;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

/**
 * Created by artur.skrzydlo on 2017-05-10.
 */
@Entity
@Getter
@Setter
public class Player extends AbstractDomainClass {

    private String userId;
    private String fullName;

    @OneToOne(cascade = CascadeType.ALL)
    private SpaceshipProtocol protocol;

}
