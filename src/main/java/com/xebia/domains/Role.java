package com.xebia.domains;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by artur.skrzydlo on 2017-07-11.
 */
@Entity
@Getter
@Setter
public class Role extends AbstractDomainClass {

    @Column(nullable = false)
    private String roleName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable
    Set<User> users = new LinkedHashSet<>();

    public void addUser(User user) {
        this.users.add(user);
        user.addRole(this);
    }

    public void removeUser(User user) {
        this.users.remove(user);
        user.getRoles().remove(this);
    }
}
