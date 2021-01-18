package org.lemanoman.videoviz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.lemanoman.videoviz.Constants;
import org.lemanoman.videoviz.Resposta;
import org.lemanoman.videoviz.dto.VideoPlaylistJS;
import org.lemanoman.videoviz.model.PlaylistModel;
import org.lemanoman.videoviz.model.VideoPlaylistModel;
import org.lemanoman.videoviz.model.VideoPlaylistPK;
import org.lemanoman.videoviz.repositories.PlaylistRepository;
import org.lemanoman.videoviz.repositories.VideoPlaylistRepository;
import org.lemanoman.videoviz.service.CheckupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = Constants.API_BASE_URL+"/checkup",produces = MediaType.APPLICATION_JSON_VALUE)

public class CheckupController {

    @Autowired
    CheckupService checkupService;

    @PostMapping(value = "/validadeLinks",consumes = MediaType.TEXT_PLAIN_VALUE)
    public Resposta validadeLinks(@RequestBody String links) {
        try{
            if(links==null||links.isEmpty()){
                return new Resposta().failed("Invalid links");
            }
            ObjectNode node = new ObjectMapper().createObjectNode();
            String[] arrayLinks = links.split("\\n");
            StringBuilder builder = new StringBuilder();
            List<String> validLinks = checkupService.validadeLinks(Arrays.asList(arrayLinks));
            if(validLinks.isEmpty()){
                return new Resposta().failed("Invalid links");
            }

            validLinks.forEach(item-> {
                builder.append(item);
                builder.append("\n");
            });
            node.put("before",arrayLinks.length);
            node.put("after",validLinks.size());
            node.put("links",builder.toString());
            return new Resposta(node).success();
        }catch (Exception ex){
            return new Resposta().failed(ex);
        }
    }

}
