package org.lemanoman.videoviz.service;

import org.lemanoman.videoviz.Utils;
import org.lemanoman.videoviz.dto.TaskNames;
import org.lemanoman.videoviz.model.ImagesModel;
import org.lemanoman.videoviz.model.LocationModel;
import org.lemanoman.videoviz.model.VideoLiteModel;
import org.lemanoman.videoviz.model.VideoModel;
import org.lemanoman.videoviz.repositories.ImagesRepository;
import org.lemanoman.videoviz.repositories.LocationRepository;
import org.lemanoman.videoviz.repositories.VideoPageableRepository;
import org.lemanoman.videoviz.repositories.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class VerifyImageTask implements Runnable {
    private VideoRepository videoRepository;
    private LocationRepository locationRepository;
    private ImagesRepository imagesRepository;
    private VideoFileService videoFileService;
    private VideoPageableRepository videoPageableRepository;
    private OnTaskExecution onTaskExecution;
    private Map<Integer,String> cacheLocation;
    private static final Logger log = LoggerFactory.getLogger(VerifyVideoFastTask.class);


    private void initCacheLocation(){
        this.cacheLocation = new HashMap<>();
        List<LocationModel> locationModelList = this.locationRepository.findAll();
        locationModelList.forEach(row -> cacheLocation.put(row.getIdLocation(),row.getPath()));
    }

    public VerifyImageTask(
            VideoRepository videoRepository,
            LocationRepository locationRepository,
            VideoPageableRepository videoPageableRepository,
            ImagesRepository imagesRepository,
            VideoFileService videoFileService,
            OnTaskExecution onTaskExecution) {
        this.videoRepository = videoRepository;
        this.locationRepository = locationRepository;
        this.videoPageableRepository = videoPageableRepository;
        this.onTaskExecution = onTaskExecution;
        this.imagesRepository = imagesRepository;
        this.videoFileService = videoFileService;

    }

    public VerifyImageTask(VideoRepository videoRepository, LocationRepository locationRepository, VideoPageableRepository videoPageableRepository) {
        this.videoRepository = videoRepository;
        this.locationRepository = locationRepository;
        this.videoPageableRepository = videoPageableRepository;
    }

    public List<VideoLiteModel> getList(int page) {
        Page<VideoLiteModel> paginator = videoPageableRepository.findAllByInvalidAndIsfileexist(0, 1, PageRequest.of(page, 500));
        log.info("Page {} of {} with a total of {} elements",page,paginator.getTotalPages(),paginator.getTotalElements());
        return paginator.toList();
    }

    private void createImageIfNeeded(Integer idVideo,String path,String code){
        if(!imageExists(path,code)){
            try {
                File imgFile = videoFileService.createPreviewImage(path,getMp4File(path,code));
                generateAndSaveMD5(idVideo,imgFile);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        generateAndSaveMD5(idVideo,getImageFile(path, code));

    }

    private void generateAndSaveMD5(Integer idVideo,File imgFile){
        ImagesModel imagesModel = imagesRepository.findByIdVideo(idVideo);
        if(imagesModel!=null && imagesModel.getMd5sum()!=null && !imagesModel.getMd5sum().isEmpty()){
            return;
        }
        String md5Sum = Utils.getMD5SumJava(imgFile);
        if(md5Sum==null|| md5Sum.isEmpty()){
            return;
        }

        if(imagesModel==null){
            imagesModel = new ImagesModel();
        }

        imagesModel.setMd5sum(md5Sum);
        imagesModel.setIdVideo(idVideo);
        imagesRepository.saveAndFlush(imagesModel);
    }

    private void doVerify(int page) {
        long start = System.currentTimeMillis();
        List<VideoLiteModel> videoList = getList(page);
        if (videoList == null || videoList.isEmpty()) {
            log.info("videoList is empty. Stopping");
            return;
        }

        int invalid = 0;
        int total = videoList.size();
        int found = 0;
        for (VideoLiteModel vm : videoList) {
            if (vm.getIdLocation() == null) continue;
            String path = cacheLocation.get(vm.getIdLocation());
            if (fileExists(path, vm.getCode())) {
                createImageIfNeeded(vm.getIdVideo(),path,vm.getCode());
                found++;
                continue;
            }
            invalid++;
        }
        log.info("Founded: {} ; Invalids: {}; Total: {}",found,invalid,total);
        log.info("Flushing...");
        long end = System.currentTimeMillis();
        log.info("Finishing page {} took {}ms",page,(end - start));
        page = page + 1;
        doVerify(page);
    }

    @Override
    public void run() {
        initCacheLocation();
        if (cacheLocation.isEmpty()) return;
        try {
            doVerify(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finish();
    }

    private void finish() {
        if (onTaskExecution != null) {
            onTaskExecution.onFinish(TaskNames.VERIFY_IMAGE_TASK);
        }
    }

    private boolean imageExists(String path, String code) {
        return getImageFile(path, code).exists();
    }

    private File getImageFile(String path, String code) {
        return  new File(path + "/image/" + code + ".png");
    }

    private File getMp4File(String path, String code) {
        return new File(path + "/mp4/" + code + ".mp4");
    }

    private boolean fileExists(String path, String code) {
        return getMp4File(path,code).exists();
    }

}
