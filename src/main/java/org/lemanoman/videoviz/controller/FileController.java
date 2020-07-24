package org.lemanoman.videoviz.controller;

import org.lemanoman.videoviz.Constants;
import org.lemanoman.videoviz.Resposta;
import org.lemanoman.videoviz.dto.FileHeaderJS;
import org.lemanoman.videoviz.dto.FileJS;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = Constants.API_BASE_URL+"/file",produces = MediaType.APPLICATION_JSON_VALUE)

public class FileController {

    @PostMapping("/get")
    public Resposta get(@RequestBody FileHeaderJS fileHeaderJS) {
        try{
            File file = new File(fileHeaderJS.getPath());
            if(file.isDirectory()){
                List<FileJS> fileList = new ArrayList<>();
                for(File subfile: file.listFiles()){
                    fileList.add(toFileJS(subfile));
                }
                return new Resposta(fileList).success();
            }else{
                return new Resposta(toFileJS(file)).success();
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return new Resposta().failed(ex);
        }
    }

    @PostMapping("/download")
    public Object  download(@RequestBody FileHeaderJS fileHeaderJS) {
        try{
            File file = new File(fileHeaderJS.getPath());
            if(!file.isDirectory()){
                return new FileSystemResource(file);
            }else{
                return new Resposta().failed("Arquivo não é um diretorio");
            }
        }catch (Exception ex){
            ex.printStackTrace();
            return new Resposta().failed(ex);
        }
    }


    private FileJS toFileJS(File file){
        FileJS fileJS = new FileJS();
        if(file.exists()){
            fileJS.setFilename(file.getName());
            fileJS.setPath(file.getAbsolutePath());
            fileJS.setSize(file.length());
            fileJS.setLastModified(new Date(file.lastModified()));
            fileJS.setDirectory(file.isDirectory());
        }
        return fileJS;
    }

}
