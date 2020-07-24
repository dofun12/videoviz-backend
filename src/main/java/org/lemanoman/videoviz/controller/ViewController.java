package org.lemanoman.videoviz.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.lemanoman.videoviz.dto.VideoJS;
import org.lemanoman.videoviz.repositories.VideoHistoryRepository;
import org.lemanoman.videoviz.repositories.VideoJDBCRepository;
import org.lemanoman.videoviz.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Controller
@RequestMapping("/view")
public class ViewController{
    @Autowired
    private VideoHistoryRepository videoHistoryRepository;

    @Autowired
    private VideoJDBCRepository videoJDBCRepository;

    @Autowired
    private VideoRepository videoRepository;


    @GetMapping("/home")
    public String home(Model model){
        return "/home";
    }

    @GetMapping("/list/{type}")
    public String list(@PathVariable("type") String type, Model model){
        List<VideoJS> listVideoJS = new ArrayList<>();
        ArrayNode listArray = videoJDBCRepository.listVideo(type,null,null);
        Iterator<JsonNode> iterator = listArray.iterator();
        while(iterator.hasNext()){
            JsonNode jsonNode = iterator.next();
            VideoJS videoJS = new VideoJS();
            videoJS.setIdVideo(jsonNode.get("idVideo").asInt());
            videoJS.setTitle(jsonNode.get("title").asText());

            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/media/image/")
                    .path(jsonNode.get("code").asText())
                    .queryParam("time", new Date().getTime()/1000)
                    .toUriString();

            videoJS.setImageUrl(imageUrl);
            videoJS.setCode(jsonNode.get("code").asText());
            listVideoJS.add(videoJS);
        }
        model.addAttribute("listVideo", listVideoJS);
        return "/list";
    }

}
