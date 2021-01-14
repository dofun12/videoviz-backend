package org.lemanoman.videoviz.service;

import org.lemanoman.videoviz.Utils;
import org.lemanoman.videoviz.dto.OnDiscovery;
import org.lemanoman.videoviz.dto.TaskNames;
import org.lemanoman.videoviz.model.LocationModel;
import org.lemanoman.videoviz.model.VideoLiteModel;
import org.lemanoman.videoviz.model.VideoModel;
import org.lemanoman.videoviz.repositories.LocationRepository;
import org.lemanoman.videoviz.repositories.VideoPageableRepository;
import org.lemanoman.videoviz.repositories.VideoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


import java.io.File;
import java.util.List;
import java.util.Objects;

public class VerifyVideoFastTask implements Runnable{
    private VideoRepository videoRepository;
    private LocationRepository locationRepository;
    private List<LocationModel> availableLocation;
    private VideoPageableRepository videoPageableRepository;
    private OnTaskExecution onTaskExecution;

    public VerifyVideoFastTask(VideoRepository videoRepository, LocationRepository locationRepository, VideoPageableRepository videoPageableRepository, OnTaskExecution onTaskExecution){
        this.videoRepository = videoRepository;
        this.locationRepository = locationRepository;
        this.videoPageableRepository = videoPageableRepository;
        this.onTaskExecution = onTaskExecution;
    }

    public VerifyVideoFastTask(VideoRepository videoRepository, LocationRepository locationRepository, VideoPageableRepository videoPageableRepository){
        this.videoRepository = videoRepository;
        this.locationRepository = locationRepository;
        this.videoPageableRepository = videoPageableRepository;
    }

    private LocationModel getByIdLocation(Integer idLocation){
        return availableLocation.stream()
                .filter(Objects::nonNull)
                .filter(locationModel -> locationModel.getIdLocation().equals(idLocation))
                .findFirst()
                .orElse(null);
    }


    public List<VideoLiteModel> getList(int page){
        return videoPageableRepository.findAllByInvalidAndIsfileexist(0,1,PageRequest.of(page,500)).toList();
    }

    private void doVerify(int page){
        long start = System.currentTimeMillis();
        List<VideoLiteModel> videoList = getList(page);
        if(videoList==null || videoList.isEmpty()) return;

        int invalid = 0;
        int valid = 0;
        int total = videoList.size();
        for(VideoLiteModel vm:videoList){
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
        videoRepository.flush();
        long end = System.currentTimeMillis();
        System.out.println("Valids: "+valid+"; Invalids: "+invalid+ ";Total: "+total+"; Took: "+(end-start)+"ms");
        page = page+1;
        doVerify(page);
    }

    @Override
    public void run() {
        availableLocation = this.locationRepository.findAll();
        if(availableLocation.isEmpty()) return;
        try {
            doVerify(0);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        finish();
    }

    private void finish(){
        if(onTaskExecution !=null){
            onTaskExecution.onFinish(TaskNames.VERIFY_VIDEO_TASK);
        }
    }

    private void setValid(VideoLiteModel vm, boolean valid){
        try {
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
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }



    private boolean fileExists(LocationModel locationModel,String code){
        return new File(locationModel.getPath()+"/mp4/"+code+".mp4").exists();
    }

}
