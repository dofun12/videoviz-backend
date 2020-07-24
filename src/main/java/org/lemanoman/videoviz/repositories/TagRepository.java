package org.lemanoman.videoviz.repositories;

import org.lemanoman.videoviz.model.TagsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<TagsModel, Integer> {
    @Override
    public Optional<TagsModel> findById(Integer id);


    @Query(value = "SELECT t.* from videoTags vt inner join tags t on vt.idTag = t.idTag where  vt.idVideo = ?1",nativeQuery = true)
    public List<TagsModel> findByIdVideo(Integer idVideo);
}