package com.spaceships.services.reposervices.role;

import com.spaceships.domains.Role;
import com.spaceships.services.reposervices.CRUDService;

/**
 * Created by artur.skrzydlo on 2017-07-11.
 */
public interface RoleRepoService extends CRUDService<Role> {

    Role findByRoleName(String name);
}
