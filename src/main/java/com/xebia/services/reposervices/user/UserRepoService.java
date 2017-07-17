package com.xebia.services.reposervices.user;

import com.xebia.domains.User;
import com.xebia.services.reposervices.CRUDService;

/**
 * Created by artur.skrzydlo on 2017-07-11.
 */
public interface UserRepoService extends CRUDService<User> {

    User findUser(String name);

}
