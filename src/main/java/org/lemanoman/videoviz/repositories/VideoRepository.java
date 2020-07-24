package org.lemanoman.videoviz.repositories;

import org.lemanoman.videoviz.model.VideoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<VideoModel, Integer> {
    @Override
    Optional<VideoModel> findById(Integer id);

    @Override
    @Query(value = "SELECT v,vu FROM VideoModel v join v.videoUrls vu ")
    List<VideoModel> findAll();


    @Query(value = "SELECT v.* from videoPlaylist vp inner join video v on v.idVideo = vp.idVideo where  vp.idPlaylist = ?1",nativeQuery = true)
    public List<VideoModel> findByIdPlaylist(Integer idPlaylist);

}
