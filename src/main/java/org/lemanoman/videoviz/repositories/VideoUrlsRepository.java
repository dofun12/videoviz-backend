package org.lemanoman.videoviz.repositories;

import org.lemanoman.videoviz.model.VideoUrlsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoUrlsRepository extends JpaRepository<VideoUrlsModel, Integer> {
    @Override
    Optional<VideoUrlsModel> findById(Integer id);



}
