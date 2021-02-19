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

    public void addToQueue(DownloadQueue queue) {


        log.info("Tentando adicionar... " + queue.getId());
        if (isDuplicated(queue)) {
            DownloadQueue tmp = downloadQueueRepository.findById(queue.getId()).get();
            tmp.setProgress(100);
            tmp.setFailed(1);
            tmp.setInProgress(0);
            tmp.setSituacao("Duplicado");
            tmp.setFinished(1);
            downloadQueueRepository.saveAndFlush(tmp);
            return;
        }
        Optional<LocationModel> olm = locationRepository.findById(queue.getIdLocation());

        if (!olm.isPresent() || fila.contains(queue.getId())) {
            log.info("O id já esta na fila " + queue.getId());
            return;
        }

        StoreVideoTask task = new StoreVideoTask(olm.get().getPath(), queue, new OnStoreResult() {
            @Override
            public void onServiceStart(DownloadQueue queue) {
                log.info("Iniciando Servico..." + queue.getId());
            }

            @Override
            public void onDownloadStart(DownloadQueue queue, long size) {
                DownloadQueue tmp = downloadQueueRepository.findById(queue.getId()).get();
                tmp.setVideoSize(String.valueOf(size));
                tmp.setDateAdded(new Date());
                tmp.setInProgress(1);
                tmp.setProgress(10);
                tmp.setSituacao("Iniciado");
                log.info(queue.getId() + ": iniciado");
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
            public void onDownloadSuccess(DownloadQueue queue) {
                DownloadQueue tmp = downloadQueueRepository.findById(queue.getId()).get();
                tmp.setProgress(60);
                tmp.setSituacao("Baixado");
                log.info(queue.getId() + ": Baixado");
                downloadQueueRepository.saveAndFlush(tmp);
            }

            @Override
            public void onPermissionSuccess(DownloadQueue queue) {
                DownloadQueue tmp = downloadQueueRepository.findById(queue.getId()).get();
                tmp.setProgress(70);
                tmp.setSituacao("Permissao OK");
                log.info(queue.getId() + ": Permissao OK");
                downloadQueueRepository.saveAndFlush(tmp);
            }

            @Override
            public void onReadyToFactoryImage(File basePath, File mp4File, DownloadQueue queue) {
                try {
                    videoFileService.createPreviewImage(basePath.getAbsolutePath(), mp4File);
                    DownloadQueue tmp = downloadQueueRepository.findById(queue.getId()).get();
                    tmp.setProgress(80);
                    tmp.setSituacao("Imagem OK");
                    log.info(queue.getId() + ": Imagem OK");
                    downloadQueueRepository.saveAndFlush(tmp);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFinished(DownloadQueue downloadQueue, File file) {
                String md5Sum = Utils.getMD5SumJava(file);
                DownloadQueue tmp = downloadQueueRepository.findById(queue.getId()).get();
                tmp.setProgress(90);
                tmp.setSituacao("Gravando registro");
                log.info(queue.getId() + ": Gravando Registro");
                downloadQueueRepository.saveAndFlush(tmp);
                removeFromUsedPages(downloadQueue.getPageUrl());

                if (jdbcRepository.getByMD5(md5Sum) != null && !jdbcRepository.getByMD5(md5Sum).isEmpty()) {
                    tmp.setProgress(100);
                    tmp.setInProgress(0);
                    tmp.setFailed(1);
                    tmp.setFinished(1);
                    log.info(queue.getId() + ": Video já existe");
                    tmp.setSituacao("Video já existe");
                    file.delete();
                    downloadQueueRepository.saveAndFlush(tmp);
                    fila.removeIf(id -> id.equals(downloadQueue.getId()));
                    return;
                }

                tmp.setProgress(95);
                tmp.setSituacao("Buscando metadata");
                log.info(queue.getId() + ": Buscando metadata");
                downloadQueueRepository.saveAndFlush(tmp);

                createVideoModel(downloadQueue, file, md5Sum, tmp, queue);
                fila.removeIf(id -> id.equals(downloadQueue.getId()));

            }
        });
        fila.add(queue.getId());
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(Constants.MAX_THREADS);
        }

        log.info("O id: " + queue.getId() + " foi adicionado na fila");
        executorService.submit(task);


    }

    private VideoModel getVideoModel(Integer idVideo){
        Optional<VideoModel> optionalVideoModel = videoRepository.findById(idVideo);
        if(!optionalVideoModel.isPresent()){
            log.error("VideoModel from id {} was not found",idVideo);
            return null;
        }
        return optionalVideoModel.get();
    }

    private void createVideoModel(DownloadQueue downloadQueue, File file, String md5Sum, DownloadQueue tmp, DownloadQueue queue) {
        try {
            if(downloadQueue.getIdVideo()==null){
                log.error("Error creating videoModel idVideo is null");
                return;
            }


            VideoModel videoModel = getVideoModel(downloadQueue.getIdVideo());
            if(videoModel==null){
                return;
            }


            ScrapResult result = null;
            try {
                result = Scrapper.getScrapResult(downloadQueue.getPageUrl());
            } catch (VideoNotFoundException | PageNotFoundException | IOException e) {
                e.printStackTrace();
            }

            fillTitle(videoModel, result);

            videoModel.setInvalid(0);
            videoModel.setIsdeleted(0);
            videoModel.setDateAdded(new Timestamp(new Date().getTime()));
            videoModel.setIsfileexist(1);
            videoModel.setMd5Sum(md5Sum);
            videoModel.setVideoSize(String.valueOf(file.length()));
            videoRepository.saveAndFlush(videoModel);

            VideoUrlsModel videoUrlsModel = new VideoUrlsModel();
            videoUrlsModel.setIdVideo(videoModel.getIdVideo());
            videoUrlsModel.setPageUrl(downloadQueue.getPageUrl());
            videoUrlsRepository.saveAndFlush(videoUrlsModel);

            tmp.setProgress(100);
            tmp.setInProgress(0);
            tmp.setFailed(0);
            tmp.setFinished(1);
            tmp.setTitle(videoModel.getTitle());
            tmp.setSituacao("Finalizado com sucesso");
            log.info(queue.getId() + ": Finalizado com sucesso");
            downloadQueueRepository.saveAndFlush(tmp);
        } catch (Exception ex) {
            file.delete();
            VideoModel videoModel = videoRepository.findById(downloadQueue.getIdVideo()).get();
            videoRepository.delete(videoModel);

            tmp.setProgress(100);
            tmp.setInProgress(0);
            tmp.setFailed(1);
            tmp.setFinished(1);
            tmp.setTitle(videoModel.getTitle());
            log.info(queue.getId() + ": Erro ao gravar");
            tmp.setSituacao("Erro ao gravar: " + ex.getMessage());
            downloadQueueRepository.saveAndFlush(tmp);
            ex.printStackTrace();
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
