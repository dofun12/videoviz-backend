package org.lemanoman.videoviz.service;

import org.lemanoman.videoviz.Constants;
import org.lemanoman.videoviz.Scrapper;
import org.lemanoman.videoviz.Utils;
import org.lemanoman.videoviz.dto.PageNotFoundException;
import org.lemanoman.videoviz.dto.ScrapResult;
import org.lemanoman.videoviz.dto.VideoNotFoundException;
import org.lemanoman.videoviz.model.DownloadQueue;
import org.lemanoman.videoviz.model.LocationModel;
import org.lemanoman.videoviz.model.VideoModel;
import org.lemanoman.videoviz.model.VideoUrlsModel;
import org.lemanoman.videoviz.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class VideoDownloadService {

    @Autowired
    private DownloadQueueRepository downloadQueueRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private VideoFileService videoFileService;

    @Autowired
    private VideoUrlsRepository videoUrlsRepository;

    @Autowired
    private VideoJDBCRepository jdbcRepository;

    private ExecutorService executorService;
    private List<Integer> fila = new ArrayList<>();
    private Set<String> usedPages = null;
    private static final Logger log = LoggerFactory.getLogger(VideoDownloadService.class);

    private void removeFromUsedPages(String page) {
        if (usedPages == null || usedPages.isEmpty()) return;
        usedPages.removeIf(tmpPage -> tmpPage.equals(page));
    }

    private boolean isDuplicated(DownloadQueue queue) {
        final String pageUrl = queue.getPageUrl();
        if (pageUrl == null || pageUrl.isEmpty()) {
            return true;
        }

        List<DownloadQueue> list = downloadQueueRepository.findByFinishedAndFailedAndPageUrl(1, 0, pageUrl);
        if (list != null && !list.isEmpty()) {
            return true;
        }

        if (usedPages == null) {
            usedPages = new HashSet<>();
            usedPages.add(queue.getPageUrl());
            return false;
        }

        Optional<String> find = usedPages.stream().filter(page -> page.equals(queue.getPageUrl()))
                .findFirst();
        if (find.isPresent()) {
            return true;
        }

        usedPages.add(queue.getPageUrl());
        return false;

    }


    public ExecutorService addToQueue(DownloadQueue queue) {


        log.info("Tentando adicionar... " + queue.getId());
        if (isDuplicated(queue)) {
            DownloadQueue tmp = downloadQueueRepository.findById(queue.getId()).get();
            tmp.setProgress(100);
            tmp.setFailed(1);
            tmp.setInProgress(0);
            tmp.setSituacao("Duplicado");
            tmp.setFinished(1);
            downloadQueueRepository.saveAndFlush(tmp);
            return null;
        }
        Optional<LocationModel> olm = locationRepository.findById(queue.getIdLocation());

        if (!olm.isPresent() || fila.contains(queue.getId())) {
            log.info("O id já esta na fila " + queue.getId());
            return null;
        }

        StoreVideoTask task = getStoreVideoTask(queue, olm);

        //runTask
        fila.add(queue.getId());
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(Constants.MAX_THREADS);
        }

        log.info("O id: " + queue.getId() + " foi adicionado na fila");
        executorService.submit(task);
        return executorService;

    }

    private void addOrModifyPageUrl(VideoModel videoModel, String pageUrl){
        VideoUrlsModel videoUrlsModel = videoUrlsRepository.findById(videoModel.getIdVideo()).orElse(null);
        if(videoUrlsModel == null){
            videoUrlsModel = new VideoUrlsModel();
            videoUrlsModel.setIdVideo(videoModel.getIdVideo());
            videoUrlsModel.setPageUrl(pageUrl);
            videoUrlsRepository.saveAndFlush(videoUrlsModel);
            return;
        }
        videoUrlsModel.setPageUrl(pageUrl);
        videoUrlsRepository.saveAndFlush(videoUrlsModel);
    }

    public StoreVideoTask getStoreVideoTask(DownloadQueue queue, Optional<LocationModel> olm) {
        return new StoreVideoTask(olm.get().getPath(), queue, new OnStoreResult() {
                @Override
                public void onServiceStart(DownloadQueue queue1) {
                    log.info("Iniciando Servico..." + queue1.getId());
                }

                @Override
                public void onDownloadStart(DownloadQueue queue1, long size) {
                    DownloadQueue tmp = downloadQueueRepository.findById(queue1.getId()).get();
                    tmp.setVideoSize(String.valueOf(size));
                    tmp.setDateAdded(new Date());
                    tmp.setInProgress(1);
                    tmp.setProgress(10);
                    tmp.setSituacao("Iniciado");
                    log.info(queue1.getId() + ": iniciado");
                    downloadQueueRepository.saveAndFlush(tmp);
                }

                @Override
                public void onDownloadError(Exception ex) {
                    DownloadQueue tmp = downloadQueueRepository.findById(queue.getId()).get();
                    tmp.setFailed(1);
                    tmp.setFinished(1);
                    tmp.setInProgress(0);
                    tmp.setSituacao("Erro download: " + ex.getMessage());
                    log.info(queue.getId() + ": Erro download");
                    downloadQueueRepository.saveAndFlush(tmp);
                }

                @Override
                public void onDownloadSuccess(DownloadQueue queue1) {
                    DownloadQueue tmp = downloadQueueRepository.findById(queue1.getId()).get();
                    tmp.setProgress(60);
                    tmp.setSituacao("Baixado");
                    log.info(queue1.getId() + ": Baixado");
                    downloadQueueRepository.saveAndFlush(tmp);
                }

                @Override
                public void onPermissionSuccess(DownloadQueue queue1) {
                    DownloadQueue tmp = downloadQueueRepository.findById(queue1.getId()).get();
                    tmp.setProgress(70);
                    tmp.setSituacao("Permissao OK");
                    log.info(queue1.getId() + ": Permissao OK");
                    downloadQueueRepository.saveAndFlush(tmp);
                }

                @Override
                public void onReadyToFactoryImage(File basePath, File mp4File, DownloadQueue queue1) {
                    try {
                        videoFileService.createPreviewImage(basePath.getAbsolutePath(), mp4File);
                        DownloadQueue tmp = downloadQueueRepository.findById(queue1.getId()).get();
                        tmp.setProgress(80);
                        tmp.setSituacao("Imagem OK");
                        log.info(queue1.getId() + ": Imagem OK");
                        downloadQueueRepository.saveAndFlush(tmp);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFinished(DownloadQueue downloadQueue, File file) {
                    LocationModel locationModel = locationRepository.findById(downloadQueue.getIdLocation()).orElse(null);
                    if(locationModel==null){
                        log.error("DownloadQueue idlocation must be setted");
                        return;
                    }
                    String md5Sum = Utils.getMD5SumJava(file);
                    DownloadQueue tmp = downloadQueueRepository.findById(queue.getId()).get();
                    tmp.setProgress(90);
                    tmp.setTmppath(file.getAbsolutePath());
                    tmp.setSituacao("Gravando registro");
                    log.info(queue.getId() + ": Gravando Registro");
                    downloadQueueRepository.saveAndFlush(tmp);
                    removeFromUsedPages(downloadQueue.getPageUrl());

                    VideoModel videoModel = videoRepository.getByMd5Sum(md5Sum);
                    if (videoModel != null && videoModel.getCode() != null) {
                        File possibleDestinationFile = videoFileService.getVideoFileByCode(locationModel.getPath(),videoModel.getCode());
                        if (possibleDestinationFile.exists() && possibleDestinationFile.length() > 0) {
                            videoModel.setInvalid(0);
                            videoModel.setIsfileexist(1);
                            addOrModifyPageUrl(videoModel,downloadQueue.getPageUrl());
                            tmp.setProgress(100);
                            tmp.setInProgress(0);
                            tmp.setFailed(0);
                            tmp.setFinished(1);


                            log.info("{} Video já existe, founded video with same md5 (idVideo: {})",queue.getId(),videoModel.getIdVideo());
                            tmp.setSituacao("Video já existe");
                            file.delete();
                            videoRepository.saveAndFlush(videoModel);
                            downloadQueueRepository.saveAndFlush(tmp);
                            fila.removeIf(id -> id.equals(downloadQueue.getId()));
                            return;
                        }

                        try {
                            tmp.setProgress(95);
                            tmp.setSituacao("Reaproveitando Metadata");
                            log.info(queue.getId() + ": Reaproveitando Metadata do idVideo {}",videoModel.getIdVideo());
                            downloadQueueRepository.saveAndFlush(tmp);
                            moveFiles(locationModel,file,videoModel.getCode());
                            videoModel.setIdLocation(downloadQueue.getIdLocation());
                            addOrModifyPageUrl(videoModel,downloadQueue.getPageUrl());

                            tmp.setProgress(100);
                            tmp.setInProgress(0);
                            tmp.setFailed(0);
                            tmp.setFinished(1);
                            tmp.setSituacao("Metadata reutilizada");
                            videoRepository.saveAndFlush(videoModel);
                            downloadQueueRepository.saveAndFlush(tmp);
                        } catch (Exception e) {
                            tmp.setProgress(95);
                            tmp.setFailed(1);
                            tmp.setFinished(1);
                            tmp.setSituacao("Erro ao copiar o arquivo");
                            log.error(queue.getId() + ": Erro ao copiar o arquivo");
                            e.printStackTrace();
                            downloadQueueRepository.saveAndFlush(tmp);
                        }
                        return;

                    }

                    createVideoModel(downloadQueue,locationModel, file, md5Sum, tmp, queue);
                    fila.removeIf(id -> id.equals(downloadQueue.getId()));

                }
            });
    }

    private VideoModel getVideoModel(Integer idVideo) {
        Optional<VideoModel> optionalVideoModel = videoRepository.findById(idVideo);
        if (!optionalVideoModel.isPresent()) {
            log.error("VideoModel from id {} was not found", idVideo);
            return null;
        }
        return optionalVideoModel.get();
    }

    private synchronized String getNextCode() {

        int max = 7;

        String oldCode = jdbcRepository.getLastCode();
        Integer intCode;
        if (oldCode == null || oldCode.equals("null")) {
            intCode = 0;
        } else {
            intCode = Integer.parseInt(oldCode);
        }

        intCode++;
        char[] chars = intCode.toString().toCharArray();
        char[] newChars = new char[max];

        for (int i = (chars.length - 1); i >= 0; i--) {
            int y = (newChars.length - chars.length);
            newChars[i + y] = chars[i];
        }
        for (int i = 0; i < (newChars.length - chars.length); i++) {
            newChars[i] = '0';
        }
        return new String(newChars).trim();
    }

    private void moveFiles(LocationModel locationModel,File file,String code){
        try {
            String oldName = file.getName().replace(".mp4", "");
            File oldImageFile = videoFileService.getImageFileByCode(locationModel.getPath(),oldName);
            if (oldImageFile != null && oldImageFile.exists()) {
                File newImage = videoFileService.getImageFileByCode(locationModel.getPath(),code);
                FileCopyUtils.copy(oldImageFile, newImage);
                log.info("Replacing {} for {}",oldImageFile.getAbsolutePath(),newImage.getAbsolutePath());
                oldImageFile.delete();
                log.info("Deleted {}",oldImageFile.getAbsolutePath());

            }
            if (file.exists()) {
                File newFile = videoFileService.getVideoFileByCode(locationModel.getPath(),code);
                FileCopyUtils.copy(file, newFile);
                log.info("Replacing {} for {}",file.getAbsolutePath(),newFile.getAbsolutePath());
                file.delete();
                log.info("Deleted {}",file.getAbsolutePath());
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    private VideoModel getAvailableVideoModel(LocationModel locationModel,int tries){
        if(tries>5){
            return null;
        }
        try {
            VideoModel videoModel = new VideoModel();
            String code = Utils.getRandomString(18);
            videoModel.setCode(code);
            videoModel.setIdLocation(locationModel.getIdLocation());
            videoRepository.saveAndFlush(videoModel);
            return videoModel;
        }catch (Exception e){
            e.printStackTrace();
            tries = tries+1;
            return getAvailableVideoModel(locationModel,tries);
        }

    }

    private VideoModel createVideoModelAndCopyFileIFNeeded(LocationModel locationModel, File file) {
        VideoModel videoModel1 = getAvailableVideoModel(locationModel,0);
        if(videoModel1==null){
            return null;
        }
        try {
            moveFiles(locationModel,file,videoModel1.getCode());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return videoModel1;

    }

    private void createVideoModel(DownloadQueue downloadQueue,LocationModel locationModel, File file, String md5Sum, DownloadQueue tmp, DownloadQueue queue) {
        try {
            VideoModel videoModel = createVideoModelAndCopyFileIFNeeded(locationModel, file);
            if(videoModel==null){
                file.delete();
                tmp.setProgress(100);
                tmp.setInProgress(0);
                tmp.setFailed(1);
                tmp.setFinished(1);
                log.info(queue.getId() + ": Algum erro aconteceu na hora de salvar o videoModel");
                tmp.setSituacao("Algum erro aconteceu na hora de salvar o videoModel");
                downloadQueueRepository.saveAndFlush(tmp);
                return;
            }
            ScrapResult result = null;
            try {
                result = Scrapper.getScrapResult(downloadQueue.getPageUrl());
            } catch (VideoNotFoundException | PageNotFoundException | IOException e) {
                e.printStackTrace();
            }

            fillTitle(videoModel, result);
            videoModel.setIdLocation(downloadQueue.getIdLocation());
            videoModel.setInvalid(0);
            videoModel.setIsdeleted(0);
            videoModel.setDateAdded(new Timestamp(new Date().getTime()));
            videoModel.setIsfileexist(1);
            videoModel.setMd5Sum(md5Sum);
            //videoModel.setVideoSize(String.valueOf(file.length()));
            videoRepository.saveAndFlush(videoModel);

            VideoUrlsModel videoUrlsModel = new VideoUrlsModel();
            videoUrlsModel.setIdVideo(videoModel.getIdVideo());
            videoUrlsModel.setPageUrl(downloadQueue.getPageUrl());
            videoUrlsRepository.saveAndFlush(videoUrlsModel);

            tmp.setProgress(100);
            tmp.setInProgress(0);
            tmp.setFailed(0);
            tmp.setIdVideo(videoModel.getIdVideo());
            tmp.setFinished(1);
            tmp.setTitle(videoModel.getTitle());
            tmp.setSituacao("Finalizado com sucesso");
            log.info(queue.getId() + ": Finalizado com sucesso");
            downloadQueueRepository.saveAndFlush(tmp);
        } catch (Exception ex) {
            ex.printStackTrace();
            file.delete();
            tmp.setProgress(100);
            tmp.setInProgress(0);
            tmp.setFailed(1);
            tmp.setFinished(1);
            log.info(queue.getId() + ": Erro ao gravar");
            tmp.setSituacao("Erro ao gravar: " + ex.getMessage());
            downloadQueueRepository.saveAndFlush(tmp);

        }
    }

    private void fillTitle(VideoModel videoModel, ScrapResult result) {
        if (result == null || result.getTitle() == null || result.getTitle().isEmpty()) {
            videoModel.setTitle("Desconhecido");
            return;
        }
        videoModel.setTitle(result.getTitle());
        String tags = "";
        if (result.getTags() != null && !result.getTags().isEmpty()) {
            StringBuilder tagsStr = new StringBuilder();
            for (String tag : result.getTags()) {
                tagsStr.append(",").append(tag);
            }
            tags = (tagsStr.substring(1));
        }
        videoModel.setOriginalTags(tags);
    }


    public void updateQueue() {
        List<DownloadQueue> downloadQueueList = downloadQueueRepository.findByFinishedAndInProgress(0, 0);
        log.info("Metendo logo.... " + downloadQueueList.size());
        for (DownloadQueue dq : downloadQueueList) {
            log.info("Adicionando..." + dq.getId());
            addToQueue(dq);
        }
    }
}
