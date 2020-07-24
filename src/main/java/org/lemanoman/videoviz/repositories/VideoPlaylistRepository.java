package org.lemanoman.videoviz.repositories;

import org.lemanoman.videoviz.model.VideoPlaylistModel;
import org.lemanoman.videoviz.model.VideoPlaylistPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface VideoPlaylistRepository extends JpaRepository<VideoPlaylistModel, VideoPlaylistPK> {
    public List<VideoPlaylistModel> findByIdPlaylist(Integer idPlaylist);
}
