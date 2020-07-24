package org.lemanoman.videoviz.test;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lemanoman.videoviz.model.PlaylistModel;
import org.lemanoman.videoviz.model.VideoPlaylistModel;
import org.lemanoman.videoviz.model.VideoPlaylistPK;
import org.lemanoman.videoviz.repositories.PlaylistRepository;
import org.lemanoman.videoviz.repositories.VideoJDBCRepository;
import org.lemanoman.videoviz.repositories.VideoPlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VideoPlaylistTest {
    @LocalServerPort
    private int port;

    @Autowired
    private VideoPlaylistRepository videoPlaylistRepository;


    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VideoJDBCRepository repository;

    @Test
    public void testCRUD() throws Exception {
        PlaylistModel playlistModel = new PlaylistModel();
        playlistModel.setName("Teste");
        playlistModel = playlistRepository.save(playlistModel);
        List<PlaylistModel> list = playlistRepository.findByName("Teste");

        Assert.assertTrue(list!=null && list.size()>0);
        ArrayNode node = repository.listVideo("new-rand",10,0);

        VideoPlaylistPK pk = new VideoPlaylistPK();
        pk.setIdPlaylist(playlistModel.getIdPlaylist());
        pk.setIdVideo(node.get(0).get("idVideo").asInt());

        Optional<VideoPlaylistModel> response = videoPlaylistRepository.findById(pk);
        if(!response.isPresent()){
            VideoPlaylistModel vpm = new VideoPlaylistModel();
            vpm.setDateAdded(new Date());
            vpm.setIdPlaylist(pk.getIdPlaylist());
            vpm.setIdVideo(pk.getIdVideo());
            videoPlaylistRepository.save(vpm);
        }


        if(list!=null){
            for(PlaylistModel pl: list){
                List<VideoPlaylistModel> videos = videoPlaylistRepository.findByIdPlaylist(pl.getIdPlaylist());
                for(VideoPlaylistModel vpm: videos){
                    videoPlaylistRepository.delete(vpm);
                }
                playlistRepository.delete(pl);
            }
        }

        List<PlaylistModel> deleted = playlistRepository.findByName("Teste");
        Assert.assertTrue(((deleted!=null && deleted.size()==0)|| deleted == null));

    }


}
