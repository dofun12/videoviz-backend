package org.lemanoman.videoviz.repositories;

import org.lemanoman.videoviz.model.VideoLiteModel;
import org.lemanoman.videoviz.model.VideoModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoPageableRepository extends PagingAndSortingRepository<VideoLiteModel, Integer> {

    Page<VideoLiteModel> findAllByInvalidAndIsfileexist(Integer invalid, Integer isfileexist, Pageable pageable);


}
