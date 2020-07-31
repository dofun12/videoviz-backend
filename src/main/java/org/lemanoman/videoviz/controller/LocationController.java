package org.lemanoman.videoviz.controller;

import org.lemanoman.videoviz.Constants;
import org.lemanoman.videoviz.Resposta;
import org.lemanoman.videoviz.model.LocationModel;
import org.lemanoman.videoviz.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Optional;

@RestController
@RequestMapping(value = Constants.API_BASE_URL + "/locations", produces = MediaType.APPLICATION_JSON_VALUE)

public class LocationController {
    @Autowired
    LocationRepository locationRepository;

    @GetMapping("/")
    public Resposta list() {
        try {
            return new Resposta(locationRepository.findAll()).success();
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @PutMapping("/")
    public Resposta edit(@RequestBody LocationModel locationModel) {
        try {
            File file = new File(locationModel.getPath());
            locationModel.setPath(file.getAbsolutePath());
            Optional<LocationModel> lmOpt = locationRepository.findById(locationModel.getIdLocation());
            if(lmOpt.isPresent()){
                if(isValidPath(locationModel)){
                    LocationModel lm = lmOpt.get();
                    lm.setContext(locationModel.getContext());
                    lm.setPath(locationModel.getPath());
                    LocationModel location = locationRepository.saveAndFlush(lm);
                    return new Resposta(location).success();
                }else{
                    return new Resposta().failed("Erro ao acessar/criar o diretorio, verifique as permissoes");
                }
            }else{
                return new Resposta().failed("Não encontrado");
            }
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    boolean isValidPath(LocationModel locationModel){
        if(locationModel!=null && locationModel.getPath()!=null){
            File dir = new File(locationModel.getPath());
            if(dir.exists() && dir.isDirectory()){
                locationModel.setPath(dir.getAbsolutePath());
                return true;
            }else{
                return dir.mkdirs();
            }
        }else{
            return false;
        }


    }

    @PostMapping("/")
    public Resposta add(@RequestBody LocationModel locationModel) {
        try {
            File file = new File(locationModel.getPath());
            locationModel.setPath(file.getAbsolutePath());
            if(isValidPath(locationModel)){
                LocationModel location = locationRepository.saveAndFlush(locationModel);
                return new Resposta(location).success();
            }else{
                return new Resposta().failed("Erro ao acessar/criar o diretorio, verifique as permissoes");
            }

        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @GetMapping("/{id}")
    public Resposta getById(@PathVariable Integer id) {
        try {
            return new Resposta(locationRepository.findById(id).get());
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @DeleteMapping("/{id}")
    public Resposta deleteById(@PathVariable Integer id) {
        try {
            locationRepository.deleteById(id);
            return new Resposta().success();
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }
}