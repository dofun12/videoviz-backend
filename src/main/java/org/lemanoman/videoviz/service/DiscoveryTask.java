package org.lemanoman.videoviz.service;

import org.lemanoman.videoviz.Utils;
import org.lemanoman.videoviz.dto.OnDiscovery;
import org.lemanoman.videoviz.model.LocationModel;
import org.lemanoman.videoviz.model.VideoModel;
import org.lemanoman.videoviz.repositories.VideoRepository;

import java.io.File;
import java.util.Objects;

public class DiscoveryTask implements Runnable{
    private LocationModel locationModel;
    private VideoRepository videoRepository;
    private OnDiscovery onDiscovery;
    private Integer max = null ;

    public DiscoveryTask(VideoRepository videoRepository, LocationModel locationModel, Integer max, OnDiscovery onDiscovery){
        this.locationModel = locationModel;
        this.videoRepository = videoRepository;
        this.onDiscovery = onDiscovery;
        this.max = max;
    }

    public DiscoveryTask(VideoRepository videoRepository, LocationModel locationModel, OnDiscovery onDiscovery){
        this.locationModel = locationModel;
        this.videoRepository = videoRepository;
        this.onDiscovery = onDiscovery;
    }

    public DiscoveryTask(VideoRepository videoRepository, LocationModel locationModel){
        this.locationModel = locationModel;
        this.videoRepository = videoRepository;
    }

    private void finish(Integer totalFiles){
        if(onDiscovery!=null){
            onDiscovery.onFinish(locationModel,totalFiles);
        }
    }

    @Override
    public void run() {
        int totalFiles = 0;
        File dir = new File(locationModel.getPath() + "/mp4");
        if(!dir.exists()){
            finish(totalFiles);
            return;
        }

        int i = 0;
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (!file.getName().endsWith(".mp4")) {
                continue;
            }
            if(max!=null && max.equals(totalFiles)){
                break;
            }
            totalFiles++;
            String md5 = Utils.getMD5SumJava(file);

            if (md5 == null) continue;

            VideoModel vm = videoRepository.getByMd5Sum(md5);
            if (vm == null) continue;
            vm.setInvalid(0);
            vm.setLocation(locationModel.getContext());
            vm.setIdLocation(locationModel.getIdLocation());
            vm.setIsfileexist(1);
            videoRepository.save(vm);
            System.out.println("Saving... "+vm.getTitle());
            if(i>=30){
                i=0;
                System.out.println("Flushing...");
                videoRepository.flush();
            }
            i++;
        }
        videoRepository.flush();
        finish(totalFiles);
    }
}
