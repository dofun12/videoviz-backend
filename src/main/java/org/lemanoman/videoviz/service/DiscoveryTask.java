package org.lemanoman.videoviz.service;

import org.lemanoman.videoviz.Utils;
import org.lemanoman.videoviz.dto.OnDiscovery;
import org.lemanoman.videoviz.dto.TaskNames;
import org.lemanoman.videoviz.model.LocationModel;
import org.lemanoman.videoviz.model.VideoModel;
import org.lemanoman.videoviz.repositories.VideoRepository;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class DiscoveryTask implements Runnable{
    private List<LocationModel> locationModelList;
    private VideoRepository videoRepository;
    private OnTaskExecution onTaskExecution;
    private Integer max = null ;

    public DiscoveryTask(VideoRepository videoRepository, List<LocationModel> locationModelList, Integer max,OnTaskExecution onTaskExecution){
        this.locationModelList = locationModelList;
        this.videoRepository = videoRepository;
        this.onTaskExecution = onTaskExecution;
        this.max = max;
    }

    public DiscoveryTask(VideoRepository videoRepository, List<LocationModel> locationModelList){
        this.locationModelList = locationModelList;
        this.videoRepository = videoRepository;
    }

    private void finish(){
        if(onTaskExecution!=null){
            onTaskExecution.onFinish(TaskNames.DISCOVERY_TASK);
        }
    }

    private void doAction(LocationModel locationModel){
        int totalFiles = 0;
        File dir = new File(locationModel.getPath() + "/mp4");
        if(!dir.exists()){
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

            VideoModel vm = videoRepository.findByMd5Sum(md5).get(0);
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

    @Override
    public void run() {
        for (LocationModel locationModel : locationModelList) {
            if(locationModel==null) continue;
            try {
                doAction(locationModel);
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        finish();
    }
}
