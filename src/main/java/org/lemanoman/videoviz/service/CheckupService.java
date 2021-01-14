package org.lemanoman.videoviz.service;

import org.lemanoman.videoviz.dto.TaskNames;
import org.lemanoman.videoviz.model.CheckupModel;
import org.lemanoman.videoviz.model.LocationModel;
import org.lemanoman.videoviz.repositories.CheckupRepository;
import org.lemanoman.videoviz.repositories.LocationRepository;
import org.lemanoman.videoviz.repositories.VideoPageableRepository;
import org.lemanoman.videoviz.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ActiveProfiles;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@ActiveProfiles("dev")
public class CheckupService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private CheckupRepository checkupRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoPageableRepository videoPageableRepository;


    private Set<Integer> locationsIdStarted;
    private Set<Integer> locationsIdFinished;
    private Integer proccessedFiles = 0;
    ExecutorService executorService = Executors.newFixedThreadPool(1);
    private Integer idCheckup = null;
    boolean isVerifyFinished = false;
    boolean isDiscoveryFinished = false;

    private OnTaskExecution onTaskExecution() {
        return (taskName) -> {
            if (TaskNames.VERIFY_VIDEO_TASK.equals(taskName)) isVerifyFinished = true;
            if (TaskNames.DISCOVERY_TASK.equals(taskName)) isDiscoveryFinished = true;
            if (isVerifyFinished && isDiscoveryFinished) {
                finish();
            }
        };
    }

    public void requestACheckup() {
        CheckupModel checkupModel = new CheckupModel();
        checkupModel.setFinished(0);
        checkupModel.setRunning(0);
        checkupModel.setStatusMessage("Pendente");
        checkupModel.setTotalVerified(0);
        checkupRepository.saveAndFlush(checkupModel);
    }

    public void runCheckoutIFPending() {
        idCheckup = null;
        isVerifyFinished = false;
        isDiscoveryFinished = false;

        List<CheckupModel> pendingList = checkupRepository.findByRunningAndFinishedAndLastVerifiedDate(0, 0, null);
        if (pendingList == null || pendingList.isEmpty()) {
            return;
        }
        locationsIdStarted = new HashSet<>();
        locationsIdFinished = new HashSet<>();

        List<LocationModel> locations = locationRepository.findAll();
        if (locations.isEmpty()) {
            return;
        }
        CheckupModel checkupTemp = pendingList.get(0);
        CheckupModel checkupModel = checkupRepository.findById(checkupTemp.getId()).orElse(null);
        if (checkupModel == null) {
            return;
        }
        idCheckup = checkupModel.getId();
        checkupModel.setRunning(1);
        checkupModel.setLastVerifiedDate(Timestamp.from(Instant.now()));
        checkupRepository.saveAndFlush(checkupModel);

        executorService.submit(new DiscoveryTask(videoRepository, locations, 1, onTaskExecution()));
        executorService.submit(new VerifyVideoFastTask(videoRepository, locationRepository, videoPageableRepository,onTaskExecution()));
        try {
            executorService.awaitTermination(30,TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void finish() {
        executorService.shutdown();
        if (idCheckup == null) {
            return;
        }
        CheckupModel checkupModel = checkupRepository.findById(idCheckup).orElse(null);
        if (checkupModel == null) {
            return;
        }
        checkupModel.setStatusMessage("Finalizado!");
        checkupModel.setRunning(0);
        checkupModel.setFinished(1);
        checkupRepository.saveAndFlush(checkupModel);
        executorService.shutdown();
    }

}
