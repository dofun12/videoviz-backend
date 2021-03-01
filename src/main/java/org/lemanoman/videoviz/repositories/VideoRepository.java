package org.lemanoman.videoviz.repositories;

import org.lemanoman.videoviz.model.VideoModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<VideoModel, Integer> {
    @Override
    Optional<VideoModel> findById(Integer id);

    @Override
    @Query(value = "SELECT v,vu FROM VideoModel v join v.videoUrls vu ")
    List<VideoModel> findAll();

    List<VideoModel> findAllByInvalidAndIsfileexist(Integer invalid, Integer isfileexist, Pageable pageable);

    @Query(value = "SELECT v,vu FROM VideoModel v join v.videoUrls vu  where v.isfileexist=1 and v.invalid = 0 and  vu.pageUrl in (:urls)")
    List<VideoModel> findAllByVideoPageUrl(@Param("urls")List<String> urls);

    List<VideoModel> findAllByIdVideoIn(Collection<Integer> idVideo);

    VideoModel getByIdVideo(Integer idVideo);

    VideoModel getByCode(String code);

    List<VideoModel> findByMd5Sum(String md5sum);

    @Query(value = "SELECT v.* from videoPlaylist vp inner join video v on v.idVideo = vp.idVideo where  vp.idPlaylist = ?1",nativeQuery = true)
    public List<VideoModel> findByIdPlaylist(Integer idPlaylist);

}
