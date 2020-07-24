package org.lemanoman.videoviz.controller;

import org.lemanoman.videoviz.Constants;
import org.lemanoman.videoviz.Resposta;
import org.lemanoman.videoviz.model.*;
import org.lemanoman.videoviz.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(value = Constants.API_BASE_URL + "/tag", produces = MediaType.APPLICATION_JSON_VALUE)

public class TagController {
    @Autowired
    TagRepository tagRepository;

    @Autowired
    TagJDBCRepository tagJDBCRepository;

    @Autowired
    VideoTagsRepository videoTagsRepository;

    @GetMapping("/")
    public Resposta list() {
        try {
            return new Resposta(tagRepository.findAll()).success();
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @GetMapping("/all")
    public Resposta listAll() {
        try {
            return new Resposta(tagJDBCRepository.allTagsExistent()).success();
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }


    @PostMapping("/")
    public Resposta add(@RequestBody TagsModel tagsModel) {
        try {
            TagsModel tag = tagRepository.saveAndFlush(tagsModel);
            return new Resposta(tag).success();
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @GetMapping("/video/{idVideo}")
    public Resposta listByVideoId(@PathVariable Integer idVideo) {
        try {
            return new Resposta(tagJDBCRepository.findSelectedByIdVideo(idVideo)).success();
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @PostMapping("/video/{idVideo}/{idTag}")
    public Resposta addVideoTag(@PathVariable Integer idVideo,@PathVariable Integer idTag) {
        try {
            VideoTagsModelPK pk = new VideoTagsModelPK(idTag,idVideo);
            Optional<VideoTagsModel> optionalVideoTagsModel = videoTagsRepository.findById(pk);
            if(optionalVideoTagsModel.isPresent()){
                VideoTagsModel vtm = optionalVideoTagsModel.get();
                return new Resposta(videoTagsRepository.saveAndFlush(vtm)).success();
            }else{
                VideoTagsModel vtm = new VideoTagsModel();
                vtm.setIdTag(idTag);
                vtm.setIdVideo(idVideo);
                return new Resposta(videoTagsRepository.saveAndFlush(vtm)).success();
            }
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @DeleteMapping("/video/{idVideo}/{idTag}")
    public Resposta deleteVideoTag(@PathVariable Integer idVideo,@PathVariable Integer idTag) {
        try {
            VideoTagsModelPK pk = new VideoTagsModelPK(idTag,idVideo);
            VideoTagsModel vtm = videoTagsRepository.findById(pk).orElse(null);
            if(vtm!=null){
                videoTagsRepository.delete(vtm);
                return new Resposta().success();
            }else{
                return new Resposta().success();
            }
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @GetMapping("/{id}")
    public Resposta getById(@PathVariable Integer id) {
        try {
            return new Resposta(tagRepository.findById(id).get());
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @DeleteMapping("/{id}")
    public Resposta deleteById(@PathVariable Integer id) {
        try {
            tagRepository.deleteById(id);
            return new Resposta().success();
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }
}