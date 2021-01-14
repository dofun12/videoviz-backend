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
import java.util.List;
import java.util.Objects;


public class VerifyVideoFastTask implements Runnable {
    private VideoRepository videoRepository;
    private LocationRepository locationRepository;
    private List<LocationModel> availableLocation;
    private VideoPageableRepository videoPageableRepository;
    private OnTaskExecution onTaskExecution;
    private static final Logger log = LoggerFactory.getLogger(VerifyVideoFastTask.class);

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
        Page<VideoLiteModel> paginator = videoPageableRepository.findAllByInvalidAndIsfileexist(0, 1, PageRequest.of(page, 500));
        log.info("Page {} of {} with a total of {} elements",page,paginator.getTotalPages(),paginator.getTotalElements());
        return paginator.toList();
    }

    private void doVerify(int page) {
        long start = System.currentTimeMillis();
        List<VideoLiteModel> videoList = getList(page);
        if (videoList == null || videoList.isEmpty()) {
            log.info("videoList is empty. Stopping");
            return;
        }

        int invalid = 0;
        int valid = 0;
        int total = videoList.size();
        int found = 0;
        for (VideoLiteModel vm : videoList) {
            if (vm.getIdLocation() == null) continue;


            LocationModel locationModel = getByIdLocation(vm.getIdLocation());
            if (fileExists(locationModel, vm.getCode())) {
                setValid(vm, true);
                found++;
                continue;
            }
            invalid++;
            setValid(vm, false);
        }
        log.info("Founded: {} ; Invalids: {}; Total: {}",found,invalid,total);
        log.info("Flushing...");
        videoRepository.flush();
        long end = System.currentTimeMillis();
        log.info("Finishing page {} took {}ms",page,(end - start));
        page = page + 1;
        doVerify(page);
    }

    @Override
    public void run() {
        availableLocation = this.locationRepository.findAll();
        if (availableLocation.isEmpty()) return;
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

    private void setValid(VideoLiteModel vm, boolean valid) {
        try {
            VideoModel vtemp = videoRepository.findById(vm.getIdVideo()).orElse(null);
            if (vtemp == null) {
                return;
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


    private boolean fileExists(LocationModel locationModel, String code) {
        return new File(locationModel.getPath() + "/mp4/" + code + ".mp4").exists();
    }

}
