package org.lemanoman.videoviz.service;

import org.lemanoman.videoviz.Utils;
import org.lemanoman.videoviz.dto.OnDiscovery;
import org.lemanoman.videoviz.model.LocationModel;
import org.lemanoman.videoviz.model.VideoModel;
import org.lemanoman.videoviz.repositories.LocationRepository;
import org.lemanoman.videoviz.repositories.VideoRepository;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class VerifyVideoFastTask implements Runnable{
    private VideoRepository videoRepository;
    private LocationRepository locationRepository;
    private List<LocationModel> availableLocation;

    public VerifyVideoFastTask(VideoRepository videoRepository, LocationRepository locationRepository){
        this.videoRepository = videoRepository;
        this.locationRepository = locationRepository;
    }

    private LocationModel getByIdLocation(Integer idLocation){
        return availableLocation.stream()
                .filter(Objects::nonNull)
                .filter(locationModel -> locationModel.getIdLocation().equals(idLocation))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void run() {
        availableLocation = this.locationRepository.findAll();
        if(availableLocation.isEmpty()) return;
        int invalid = 0;
        int valid = 0;
        int total = 0;
        List<VideoModel> videoList = videoRepository.findAllByInvalidAndIsfileexist(0,1);
        total = videoList.size();
        for(VideoModel vm:videoList){
            if(vm.getIdLocation()==null) continue;

            LocationModel locationModel = getByIdLocation(vm.getIdLocation());
            if(fileExists(locationModel,vm.getCode())){
               setValid(vm,true);
               System.out.println("Found: "+vm.getTitle());
               continue;
            }
            invalid++;
            setValid(vm,false);
        }
        System.out.println("Valids: "+valid+"; Invalids: "+invalid+ ";Total: "+total);
        videoRepository.flush();
    }

    private void setValid(VideoModel vm, boolean valid){
        VideoModel vtemp = videoRepository.findById(vm.getIdVideo()).orElse(null);
        if(vtemp==null){
            return;
        }
        if(valid){
            vtemp.setInvalid(0);
            vtemp.setIsfileexist(1);
            videoRepository.save(vtemp);
            return;
        }

        vtemp.setInvalid(1);
        vtemp.setIsfileexist(0);
        videoRepository.save(vtemp);
    }



    private boolean fileExists(LocationModel locationModel,String code){
        return new File(locationModel.getPath()+"/mp4/"+code+".mp4").exists();
    }

}
