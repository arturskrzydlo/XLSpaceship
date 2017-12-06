package com.spaceships.services.reposervices.user;

import com.spaceships.domains.User;
import com.spaceships.services.reposervices.CRUDService;

/**
 * Created by artur.skrzydlo on 2017-07-11.
 */
public interface UserRepoService extends CRUDService<User> {

    User findUser(String name);

}
