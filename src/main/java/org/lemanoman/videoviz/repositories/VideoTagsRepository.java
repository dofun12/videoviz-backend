package org.lemanoman.videoviz.repositories;

import org.lemanoman.videoviz.model.VideoTagsModel;
import org.lemanoman.videoviz.model.VideoTagsModelPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface VideoTagsRepository extends JpaRepository<VideoTagsModel, VideoTagsModelPK> {
    public List<VideoTagsModel> findByIdVideo(Integer idVideo);
}
