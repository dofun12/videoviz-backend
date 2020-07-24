package org.lemanoman.videoviz.repositories;

import org.lemanoman.videoviz.model.VideoTagsModel;
import org.lemanoman.videoviz.model.VideoTagsModelPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface VideoTagsRepository extends JpaRepository<VideoTagsModel, VideoTagsModelPK> {

}
