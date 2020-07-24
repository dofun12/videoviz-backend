package org.lemanoman.videoviz.controller;

import org.lemanoman.videoviz.Constants;
import org.lemanoman.videoviz.Resposta;
import org.lemanoman.videoviz.model.DownloadQueue;
import org.lemanoman.videoviz.repositories.*;
import org.lemanoman.videoviz.service.VideoDownloadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = Constants.API_BASE_URL + "/downloadQueue", produces = MediaType.APPLICATION_JSON_VALUE)

public class DownloadQueueController {

    @Autowired
    private DownloadQueueRepository downloadQueueRepository;

    @Autowired
    private VideoDownloadService videoDownloadService;


    @GetMapping("/")
    public Resposta listAll() {
        try {
            return new Resposta(downloadQueueRepository.findAll(PageRequest.of(0,300,Sort.by("id").descending())).toList()).success();
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @GetMapping("/{id}")
    public Resposta getById(@PathVariable("id") Integer id) {
        try {
            return new Resposta(downloadQueueRepository.findById(id)).success();
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @GetMapping("/atualizarColetor")
    public Resposta getAtualizarColetor() {
        try {
            videoDownloadService.updateQueue();
            return new Resposta().success();
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @PutMapping("/{id}")
    public Resposta salvarAlteracoes(@PathVariable("id") Integer id,@RequestBody DownloadQueue dq) {
        try {
            DownloadQueue tmp = downloadQueueRepository.findById(id).get();
            tmp.setInProgress(dq.getInProgress());
            tmp.setFinished(dq.getFinished());
            tmp.setFailed(dq.getFailed());
            tmp.setVideoUrl(dq.getVideoUrl());
            tmp.setSituacao(dq.getSituacao());
            tmp.setPageUrl(dq.getPageUrl());
            downloadQueueRepository.saveAndFlush(dq);
            return new Resposta().success();
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

}
