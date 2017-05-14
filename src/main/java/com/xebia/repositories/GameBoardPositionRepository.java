package com.xebia.repositories;

import com.xebia.domains.GameBoardPosition;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
public interface GameBoardPositionRepository extends CrudRepository<GameBoardPosition, Integer> {

}
