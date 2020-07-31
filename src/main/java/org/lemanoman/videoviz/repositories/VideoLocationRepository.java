package org.lemanoman.videoviz.repositories;

import org.lemanoman.videoviz.model.VideoLocationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface VideoLocationRepository extends JpaRepository<VideoLocationModel, Integer>, JpaSpecificationExecutor<VideoLocationModel> {

}