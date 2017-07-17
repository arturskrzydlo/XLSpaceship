package com.xebia.services.reposervices.role;

import com.xebia.domains.Role;
import com.xebia.services.reposervices.CRUDService;

/**
 * Created by artur.skrzydlo on 2017-07-11.
 */
public interface RoleRepoService extends CRUDService<Role> {

    Role findByRoleName(String name);
}
