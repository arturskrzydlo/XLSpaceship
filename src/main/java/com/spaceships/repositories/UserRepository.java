package com.spaceships.repositories;

import com.spaceships.domains.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by artur.skrzydlo on 2017-07-11.
 */
public interface UserRepository extends CrudRepository<User, Integer> {

    User findByName(String name);
}
