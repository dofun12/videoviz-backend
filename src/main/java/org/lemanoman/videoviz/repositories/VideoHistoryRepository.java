package org.lemanoman.videoviz.repositories;

import org.lemanoman.videoviz.model.VideoHistoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VideoHistoryRepository extends JpaRepository<VideoHistoryModel, Integer> {


}
