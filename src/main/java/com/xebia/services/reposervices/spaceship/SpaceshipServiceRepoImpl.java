package com.xebia.services.reposervices.spaceship;

import com.xebia.domains.Spaceship;
import com.xebia.repositories.SpaceshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by artur.skrzydlo on 2017-05-11.
 */
@Service
public class SpaceshipServiceRepoImpl implements SpaceshipRepoService {

    private SpaceshipRepository spaceshipRepository;

    @Autowired
    public void setSpaceshipRepository(SpaceshipRepository spaceshipRepository) {
        this.spaceshipRepository = spaceshipRepository;
    }

    @Override
    public List<Spaceship> listAll() {
        return StreamSupport.stream(spaceshipRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public Spaceship getByGameId(Integer id) {
        return spaceshipRepository.findOne(id);
    }

    @Override
    public Spaceship saveOrUpdate(Spaceship domainObject) {
        return spaceshipRepository.save(domainObject);
    }

    @Override
    public void delete(Integer id) {
        spaceshipRepository.delete(id);
    }
}
