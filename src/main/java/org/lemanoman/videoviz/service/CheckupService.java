package org.lemanoman.videoviz.service;

import org.lemanoman.videoviz.dto.OnDiscovery;
import org.lemanoman.videoviz.model.CheckupModel;
import org.lemanoman.videoviz.model.LocationModel;
import org.lemanoman.videoviz.repositories.CheckupRepository;
import org.lemanoman.videoviz.repositories.LocationRepository;
import org.lemanoman.videoviz.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.test.*;
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
    private Set<Integer> locationsIdStarted;
    private Set<Integer> locationsIdFinished;
    private Integer proccessedFiles = 0;
    ExecutorService executorService = Executors.newFixedThreadPool(1);

    private OnDiscovery onDiscovery(Integer idCheckup) {
        return (locationModel, totalFiles) -> {
            locationsIdFinished.add(locationModel.getIdLocation());
            proccessedFiles = totalFiles + proccessedFiles;
            if (locationsIdStarted.size() == locationsIdFinished.size()) {
                finish(idCheckup);
            }
        };
    }

    public void requestACheckup(){
        CheckupModel checkupModel = new CheckupModel();
        checkupModel.setFinished(0);
        checkupModel.setRunning(0);
        checkupModel.setStatusMessage("Pendente");
        checkupModel.setTotalVerified(0);
        checkupRepository.saveAndFlush(checkupModel);
    }

    public void runCheckoutIFPending() {
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
        if(checkupModel==null){
            return;
        }
        checkupModel.setRunning(1);
        checkupModel.setLastVerifiedDate(Timestamp.from(Instant.now()));
        checkupRepository.saveAndFlush(checkupModel);

        for (LocationModel locationModel : locations) {
            locationsIdStarted.add(locationModel.getIdLocation());
            executorService.submit(new DiscoveryTask(videoRepository, locationModel,1 , onDiscovery(checkupModel.getId())));
        }
        try {
            executorService.awaitTermination(60, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void finish(Integer idCheckup) {
        CheckupModel checkupModel = checkupRepository.findById(idCheckup).orElse(null);
        if(checkupModel==null){
            return;
        }
        checkupModel.setRunning(0);
        checkupModel.setFinished(1);
        checkupRepository.saveAndFlush(checkupModel);
        executorService.shutdown();
    }

}
