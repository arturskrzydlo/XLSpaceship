package com.spaceships.repositories;

import com.spaceships.domains.Role;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by artur.skrzydlo on 2017-07-11.
 */
public interface RoleRepository extends CrudRepository<Role, Integer> {

    Role findByRoleName(String name);
}
