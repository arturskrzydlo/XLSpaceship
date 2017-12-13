package com.xebia.repositories;

import com.xebia.domains.Spaceship;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public interface SpaceshipRepository extends CrudRepository<Spaceship, Integer> {
}
