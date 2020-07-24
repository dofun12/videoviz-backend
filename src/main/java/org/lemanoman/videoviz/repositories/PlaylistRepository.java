package org.lemanoman.videoviz.repositories;

import org.lemanoman.videoviz.model.PlaylistModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository<PlaylistModel, Integer> {
    @Override
    public Optional<PlaylistModel> findById(Integer id);

    public List<PlaylistModel> findByName(String name);


}
