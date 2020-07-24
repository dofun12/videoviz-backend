package org.lemanoman.videoviz.controller;

import org.lemanoman.videoviz.Constants;
import org.lemanoman.videoviz.Resposta;
import org.lemanoman.videoviz.dto.VideoPlaylistJS;
import org.lemanoman.videoviz.model.PlaylistModel;
import org.lemanoman.videoviz.model.VideoPlaylistModel;
import org.lemanoman.videoviz.model.VideoPlaylistPK;
import org.lemanoman.videoviz.repositories.PlaylistRepository;
import org.lemanoman.videoviz.repositories.VideoPlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@RestController
@RequestMapping(value = Constants.API_BASE_URL+"/playlist",produces = MediaType.APPLICATION_JSON_VALUE)

public class PlaylistController {
    @Autowired
    PlaylistRepository playlistRepository;

    @Autowired
    VideoPlaylistRepository videoPlaylistRepository;

    @GetMapping("/")
    public Resposta listAll() {
        try{
            return new Resposta(playlistRepository.findAll()).success();
        }catch (Exception ex){
            return new Resposta().failed(ex);
        }
    }

    @PostMapping("/")
    public Resposta add(@RequestBody PlaylistModel playlistModel) {
        try{
            PlaylistModel play = playlistRepository.saveAndFlush(playlistModel);
            return new Resposta(play).success();
        }catch (Exception ex){
            return new Resposta().failed(ex);
        }
    }

    @PostMapping("/addVideo")
    public Resposta addVideo(@RequestBody VideoPlaylistJS videoPlaylistJS) {
        try{
            VideoPlaylistPK pk = new VideoPlaylistPK();
            pk.setIdVideo(videoPlaylistJS.getIdVideo());
            pk.setIdPlaylist(videoPlaylistJS.getIdPlaylist());

            Optional<VideoPlaylistModel> response = videoPlaylistRepository.findById(pk);
            if(!response.isPresent()){
                VideoPlaylistModel vpm = new VideoPlaylistModel();
                vpm.setIdVideo(pk.getIdVideo());
                vpm.setIdPlaylist(pk.getIdPlaylist());
                vpm.setDateAdded(new Date());
                videoPlaylistRepository.saveAndFlush(vpm);
                return new Resposta(vpm).success();
            }
            return new Resposta(response.get()).success();
        }catch (Exception ex){
            return new Resposta().failed(ex);
        }
    }

    @GetMapping("/{id}")
    public Resposta getById(@PathVariable Integer id) {
        try{
            return new Resposta(playlistRepository.findById(id).get());
        }catch (Exception ex){
            return new Resposta().failed(ex);
        }
    }

    @DeleteMapping("/{id}")
    public Resposta deleteById(@PathVariable Integer id) {
        try{
            playlistRepository.deleteById(id);
            return new Resposta().success();
        }catch (Exception ex){
            return new Resposta().failed(ex);
        }
    }
}
