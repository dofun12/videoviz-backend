package org.lemanoman.videoviz.service;

import org.lemanoman.videoviz.Utils;
import org.lemanoman.videoviz.model.LocationModel;
import org.lemanoman.videoviz.model.VideoModel;
import org.lemanoman.videoviz.repositories.VideoRepository;

import java.io.File;
import java.util.Objects;

public class DiscoveryTask implements Runnable{
    private LocationModel locationModel;
    private VideoRepository videoRepository;


    public DiscoveryTask(VideoRepository videoRepository, LocationModel locationModel){
        this.locationModel = locationModel;
        this.videoRepository = videoRepository;
    }

    @Override
    public void run() {
        File dir = new File(locationModel.getPath() + "/mp4");
        if(!dir.exists()){
            return;
        }

        int i = 0;
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (!file.getName().endsWith(".mp4")) {
                continue;
            }
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
    }
}
