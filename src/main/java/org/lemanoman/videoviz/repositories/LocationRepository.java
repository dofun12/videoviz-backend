package org.lemanoman.videoviz.repositories;

import org.lemanoman.videoviz.model.LocationModel;
import org.lemanoman.videoviz.model.PlaylistModel;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<LocationModel, Integer> {
    @Override
    Optional<LocationModel> findById(Integer id);

    Optional<LocationModel> findByContext(String context);

}
