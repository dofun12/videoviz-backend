package org.lemanoman.videoviz.repositories;

import org.lemanoman.videoviz.model.ImagesModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagesRepository extends JpaRepository <ImagesModel, Integer> {

    public ImagesModel findByIdVideo(Integer idVideo);
}
