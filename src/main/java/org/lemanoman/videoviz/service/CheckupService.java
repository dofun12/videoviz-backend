package org.lemanoman.videoviz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import org.lemanoman.videoviz.Utils;
import org.lemanoman.videoviz.dto.Duplicated;
import org.lemanoman.videoviz.dto.MergeStrategy;
import org.lemanoman.videoviz.dto.TaskNames;
import org.lemanoman.videoviz.model.CheckupModel;
import org.lemanoman.videoviz.model.LocationModel;
import org.lemanoman.videoviz.model.VideoModel;
import org.lemanoman.videoviz.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CheckupService {

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private CheckupRepository checkupRepository;

    @Autowired
    private ImagesRepository imagesRepository;

    @Autowired
    private VideoFileService videoFileService;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoPageableRepository videoPageableRepository;

    private static final String OPERATION_CHECKUP_IMAGES = "CHECKUP_IMAGES";
    private static final String OPERATION_CHECKUP_VIDEOS = "CHECKUP_VIDEOS";
    private static final String OPERATION_DISCOVERY = "DISCOVERY";

    private static final Logger log = LoggerFactory.getLogger(CheckupService.class);
    private Set<Integer> locationsIdStarted;
    private Set<Integer> locationsIdFinished;
    private Integer proccessedFiles = 0;
    private ExecutorService executorService = Executors.newFixedThreadPool(1);
    private Integer idCheckup = null;
    boolean isVerifyFinished = false;
    boolean isDiscoveryFinished = false;

    public enum CheckupOperation {
        CHECKUP_IMAGES, CHECKUP_VIDEOS, DISCOVERY
    }

    private OnTaskExecution onTaskExecution() {
        return (taskName) -> finish(false);
    }

    private void print(ObjectNode objectNode){
        final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {
            System.out.println(mapper.writeValueAsString(objectNode));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    public ObjectNode resolveDuplicate(Duplicated duplicated){
        List<VideoModel> videoModelList = videoRepository.findAllByIdVideoIn(duplicated.getIdVideoList());
        return videoModelList.stream().map(videoModel -> videoService.getVideoInfo(videoModel)).reduce((v1, v2) -> {
            System.out.println("V1: ");
            print(v1);
            System.out.println("======================= x ==================");
            print(v2);
            ObjectNode videoNode = v1;
            if(v2.get("fileexists").asBoolean()){
                videoNode = v2;
            }
            videoNode.put("pageUrl",(String) merge(v1,v2,"pageUrl",MergeStrategy.TEXT_BIGGER_OR_NOT_NULL));
            videoNode.put("totalWatched",(Integer) merge(v1,v2,"totalWatched",MergeStrategy.INT_SUM));
            videoNode.put("tags",(String) merge(v1,v2,"tags",MergeStrategy.TEXT_INCREMENT_LIST));
            videoNode.put("original_tags",(String) merge(v1,v2,"original_tags",MergeStrategy.TEXT_INCREMENT_LIST));
            videoNode.put("favorite",(Integer) merge(v1,v2,"favorite",MergeStrategy.INT_BIGGER_OR_NOTNULL));
            videoNode.put("rating",(Integer) merge(v1,v2,"rating",MergeStrategy.INT_BIGGER_OR_NOTNULL));
            videoNode.put("duplicated_idVideos",(String) merge(v1,v2,"idVideo",MergeStrategy.TEXT_INCREMENT_LIST));

            System.out.println("======================= Result: ==================");
            print(videoNode);

            return videoNode;
        }).orElse(null);
    }



    private Object merge(ObjectNode objectNode, ObjectNode objectNode2, String key, MergeStrategy strategy){
        switch (strategy){
            case INT_BIGGER_OR_NOTNULL:
                return keepBiggerOrNotNull(
                   Utils.toInt(objectNode.get(key).asInt()),
                   Utils.toInt(objectNode2.get(key).asInt())
                );
            case TEXT_INCREMENT_LIST:
                return incrementTags(objectNode.get(key).asText(),objectNode2.get(key).asText());
            case INT_SUM:
                return sumValues(
                   Utils.toInt(objectNode.get(key).asInt()),
                   Utils.toInt(objectNode2.get(key).asInt())
                );
            case TEXT_BIGGER_OR_NOT_NULL:
                return biggerLengthOrNotNull(
                        objectNode.get(key).asText(),
                        objectNode2.get(key).asText()
                );
            default: return null;
        }
    }

    private Integer sumValues(Integer value,Integer value2){
        if(value==null){
            return value2;
        }
        if(value2==null){
            return value;
        }
        return value2+value;
    }

    private String biggerLengthOrNotNull(String value,String value2){
        if(value==null){
            return value2;
        }
        if(value2==null){
            return value;
        }
        if(value.length()>value2.length()){
            return value;
        }
        return value2;
    }

    private Integer keepBiggerOrNotNull(Integer value,Integer value2){
        if(value==null){
            return value2;
        }
        if(value2==null){
            return value;
        }
        if(value>value2){
            return value;
        }
        return value2;
    }

    private String incrementTags(String tags,String anotherTags){
        Set<String> mainList = new HashSet<>();
        if(tags.isEmpty()){
            return anotherTags;
        }
        if(anotherTags.isEmpty()){
            return tags;
        }
        if(!tags.contains(",") && !"null".equals(tags)){
            mainList.add(tags);
        }

        if(!anotherTags.contains(",") && !"null".equals(anotherTags)){
            mainList.add(anotherTags);
        }

        mainList.addAll(Arrays.stream(tags.split(",")).collect(Collectors.toSet()));
        mainList.addAll(Arrays.stream(anotherTags.split(",")).collect(Collectors.toSet()));

        return mainList.stream().reduce((s, s2) -> s = s.trim()+","+s2.trim()).orElse(null);

    }

    public void clean() {
        List<CheckupModel> invalids = checkupRepository.findByRunningAndFinished(1, 0);
        if (invalids == null || invalids.isEmpty()) {
            return;
        }

        for (CheckupModel cm : invalids) {
            cm.setRunning(0);
            cm.setFinished(0);
            checkupRepository.save(cm);
        }
        checkupRepository.flush();
    }


    public void requestACheckup(CheckupOperation operation) {
        CheckupModel checkupModel = new CheckupModel();
        checkupModel.setFinished(0);
        checkupModel.setRunning(0);
        String operationValue = null;
        switch (operation) {
            case DISCOVERY:
                operationValue = OPERATION_DISCOVERY;
                break;
            case CHECKUP_IMAGES:
                operationValue = OPERATION_CHECKUP_IMAGES;
                break;
            case CHECKUP_VIDEOS:
                operationValue = OPERATION_CHECKUP_VIDEOS;
                break;
        }
        checkupModel.setOperation(operationValue);
        checkupModel.setStatusMessage("Pendente");
        checkupModel.setTotalVerified(0);
        checkupRepository.saveAndFlush(checkupModel);
    }

    public void runCheckoutIFPending() {
        idCheckup = null;
        isVerifyFinished = false;
        isDiscoveryFinished = false;

        List<CheckupModel> pendingList = checkupRepository.findByRunningAndFinished(0, 0);
        if (pendingList == null || pendingList.isEmpty()) {
            log.info("Not checkups pending...");
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

        if (Objects.equals(checkupModel.getOperation(), OPERATION_CHECKUP_VIDEOS)){
            checkupVideos();
            return;
        }
        if (Objects.equals(checkupModel.getOperation(), OPERATION_CHECKUP_IMAGES)){
            checkupImages();
            return;
        }
        if (Objects.equals(checkupModel.getOperation(), OPERATION_DISCOVERY)){
            discovery();
        }

    }

    private void checkupImages() {
        executorService.submit(new VerifyImageTask(videoRepository, locationRepository, videoPageableRepository, imagesRepository, videoFileService, onTaskExecution()));
        try {
            executorService.awaitTermination(30, TimeUnit.MINUTES);
            finish(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void discovery() {
        executorService.submit(new DiscoveryTask(videoRepository, locationRepository.findAll(), 1, onTaskExecution()));
        try {
            executorService.awaitTermination(30, TimeUnit.MINUTES);
            finish(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void checkupVideos() {
        executorService.submit(new VerifyVideoFastTask(videoRepository, locationRepository, videoPageableRepository, onTaskExecution()));
        try {
            executorService.awaitTermination(30, TimeUnit.MINUTES);
            finish(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void finish(boolean timeout) {
        executorService.shutdown();
        if (idCheckup == null) {
            return;
        }
        CheckupModel checkupModel = checkupRepository.findById(idCheckup).orElse(null);
        if (checkupModel == null) {
            return;
        }
        String message = "Finalizado!";
        if(timeout){
            message = "Timeouted :P";
        }
        checkupModel.setStatusMessage(message);
        checkupModel.setRunning(0);
        checkupModel.setFinished(1);
        checkupRepository.saveAndFlush(checkupModel);
        executorService.shutdown();
    }

    public List<String> validadeLinks(List<String> links) {
        int i = 0;
        int tot = 0;
        int max = 10;
        String[][] matrix = new String[links.size()][max];
        for (String link : links) {
            if (tot == max) {
                i++;
                tot = 0;
            }
            matrix[i][tot] = link.trim().replaceAll("\\n", "").replaceAll("\\r", "").replaceAll("\\t", "");
            tot++;
        }
        Set<String> uniqueLinks = new HashSet<>();
        for (String[] vector : matrix) {
            List<String> vectorList = Arrays.asList(vector);
            if (vectorList.isEmpty()) {
                continue;
            }
            uniqueLinks.addAll(checkLinks(vectorList));
        }
        return new ArrayList<>(uniqueLinks);
    }

    private List<String> checkLinks(List<String> list) {
        list = list.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (list.isEmpty()) {
            return new ArrayList<>();
        }


        List<VideoModel> videoModels = videoRepository.findAllByVideoPageUrl(list);
        Set<String> founded = videoModels.stream().filter(Objects::nonNull).map(item -> item.getVideoUrls().getPageUrl()).collect(Collectors.toSet());
        list.stream().filter(Objects::nonNull).map(link -> {
            if (founded.contains(link)) {
                return link + ": EXISTS";
            }
            return link + ":  NEW";
        }).forEach(System.out::println);
        System.out.println("========================");
        return list.stream()
                .filter(item -> !founded.contains(item))
                .collect(Collectors.toList());
    }


}
