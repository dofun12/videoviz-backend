package org.lemanoman.videoviz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.lemanoman.videoviz.Constants;
import org.lemanoman.videoviz.Resposta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = Constants.API_BASE_URL+"/version",produces = MediaType.APPLICATION_JSON_VALUE)
public class VersionController {
    @Value("${app.version}")
    private String version;

    @Value("${app.name}")
    private String appName;

    @GetMapping("/")
    public Resposta listAll() {
        try{
            ObjectNode versionNode = new ObjectMapper().createObjectNode();
            versionNode.put("version",version);
            versionNode.put("appName",appName);
            return new Resposta(versionNode).success();
        }catch (Exception ex){
            return new Resposta().failed(ex);
        }
    }

}
