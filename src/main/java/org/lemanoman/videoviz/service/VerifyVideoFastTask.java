package org.lemanoman.videoviz.service;

import org.lemanoman.videoviz.ServerInitializer;
import org.lemanoman.videoviz.Utils;
import org.lemanoman.videoviz.dto.OnDiscovery;
import org.lemanoman.videoviz.dto.TaskNames;
import org.lemanoman.videoviz.model.LocationModel;
import org.lemanoman.videoviz.model.VideoLiteModel;
import org.lemanoman.videoviz.model.VideoModel;
import org.lemanoman.videoviz.repositories.LocationRepository;
import org.lemanoman.videoviz.repositories.VideoPageableRepository;
import org.lemanoman.videoviz.repositories.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


import java.io.File;
import java.util.*;


public class VerifyVideoFastTask implements Runnable {
    private VideoRepository videoRepository;
    private LocationRepository locationRepository;
    private List<LocationModel> availableLocation;
    private VideoPageableRepository videoPageableRepository;
    private OnTaskExecution onTaskExecution;
    private Map<Integer, String> cacheLocation;
    private static final Logger log = LoggerFactory.getLogger(VerifyVideoFastTask.class);


    private void initCacheLocation() {
        this.cacheLocation = new HashMap<>();
        List<LocationModel> locationModelList = this.locationRepository.findAll();
        locationModelList.forEach(row -> cacheLocation.put(row.getIdLocation(), row.getPath()));
    }

    public VerifyVideoFastTask(VideoRepository videoRepository, LocationRepository locationRepository, VideoPageableRepository videoPageableRepository, OnTaskExecution onTaskExecution) {
        this.videoRepository = videoRepository;
        this.locationRepository = locationRepository;
        this.videoPageableRepository = videoPageableRepository;
        this.onTaskExecution = onTaskExecution;

    }

    public VerifyVideoFastTask(VideoRepository videoRepository, LocationRepository locationRepository, VideoPageableRepository videoPageableRepository) {
        this.videoRepository = videoRepository;
        this.locationRepository = locationRepository;
        this.videoPageableRepository = videoPageableRepository;
    }

    private LocationModel getByIdLocation(Integer idLocation) {
        return availableLocation.stream()
                .filter(Objects::nonNull)
                .filter(locationModel -> locationModel.getIdLocation().equals(idLocation))
                .findFirst()
                .orElse(null);
    }


    public List<VideoLiteModel> getList(int page) {
        Page<VideoLiteModel> paginator = videoPageableRepository.findAll(PageRequest.of(page, 500));
        log.info("Page {} of {} with a total of {} elements", page, paginator.getTotalPages(), paginator.getTotalElements());
        return paginator.toList();
    }

    private boolean hasDifference(VideoLiteModel before, boolean isValidNow) {
        if (!isValidNow && Objects.equals(before.getInvalid(), 0) && Objects.equals(before.getInvalid(), 1)) {
            return true;
        }
        return isValidNow && Objects.equals(before.getInvalid(), 1) && Objects.equals(before.getIsfileexist(), 1);
    }

    private void doVerify(int page) {
        long start = System.currentTimeMillis();
        List<VideoLiteModel> videoList = getList(page);
        if (videoList == null || videoList.isEmpty()) {
            log.info("videoList is empty. Stopping");
            return;
        }


        int invalid = 0;
        int skipped = 0;
        int total = videoList.size();
        int found = 0;
        for (VideoLiteModel vm : videoList) {
            if (vm.getIdLocation() == null) continue;
            String path = cacheLocation.get(vm.getIdLocation());
            boolean isValid = false;

            if (fileExists(path, vm.getCode())) {
                isValid = true;
            }

            LocationModel lm = searchByLocations(path,vm.getCode());
            if(lm!=null){
                setValid(vm,true,lm);
                continue;
            }

            if (!hasDifference(vm, isValid)) {
                skipped++;
                continue;
            }

            if (isValid) {
                setValid(vm, true);
                found++;
                continue;
            }

            invalid++;
            setValid(vm, false);
        }
        log.info("Founded: {} ; Invalids: {}; Skipped: {}; Total: {}", found, invalid,skipped, total);
        log.info("Flushing...");
        if(total > skipped){
            videoRepository.flush();
        }

        long end = System.currentTimeMillis();
        log.info("Finishing page {} took {}ms", page, (end - start));
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
            onTaskExecution.onFinish(TaskNames.VERIFY_VIDEO_TASK);
        }
    }

    private void setValid(VideoLiteModel vm, boolean valid){
        setValid(vm,valid,null);
    }

    private void setValid(VideoLiteModel vm, boolean valid,LocationModel locationModel) {
        try {
            VideoModel vtemp = videoRepository.findById(vm.getIdVideo()).orElse(null);
            if (vtemp == null) {
                return;
            }
            if(locationModel!=null){
                vtemp.setIdLocation(locationModel.getIdLocation());
            }
            if (valid) {
                vtemp.setInvalid(0);
                vtemp.setIsfileexist(1);
                videoRepository.save(vtemp);
                return;
            }

            vtemp.setInvalid(1);
            vtemp.setIsfileexist(0);
            videoRepository.save(vtemp);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }


    private boolean fileExists(String path, String code) {
        return new File(path + "/mp4/" + code + ".mp4").exists();
    }

    private LocationModel searchByLocations(String path, String code) {
        for(Map.Entry<Integer,String> entry: cacheLocation.entrySet()){
            if(fileExists(entry.getValue(),code)){
                LocationModel lm = new LocationModel();
                lm.setPath(path);
                lm.setIdLocation(entry.getKey());
                lm.setPath(entry.getValue());
                return lm;
            };
        }
        return null;
    }

}
