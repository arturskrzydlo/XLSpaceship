package com.spaceships.domains;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by artur.skrzydlo on 2017-07-10.
 */
@Entity
@Getter
@Setter
public class User extends AbstractDomainClass {

    @Column(nullable = false)
    private String name;

    @Transient
    private String password;

    @Column(nullable = false)
    private String encryptedPassword;
    private Boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable
    private Set<Role> roles = new LinkedHashSet<>();

    public void addRole(Role role) {
        roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
        role.getUsers().remove(this);
    }
}
