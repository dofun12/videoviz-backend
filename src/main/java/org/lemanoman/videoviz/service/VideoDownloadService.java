package org.lemanoman.videoviz.service;

import org.lemanoman.videoviz.Constants;
import org.lemanoman.videoviz.Scrapper;
import org.lemanoman.videoviz.Utils;
import org.lemanoman.videoviz.dto.PageNotFoundException;
import org.lemanoman.videoviz.dto.ScrapResult;
import org.lemanoman.videoviz.dto.VideoNotFoundException;
import org.lemanoman.videoviz.model.DownloadQueue;
import org.lemanoman.videoviz.model.VideoModel;
import org.lemanoman.videoviz.model.VideoUrlsModel;
import org.lemanoman.videoviz.repositories.DownloadQueueRepository;
import org.lemanoman.videoviz.repositories.VideoJDBCRepository;
import org.lemanoman.videoviz.repositories.VideoRepository;
import org.lemanoman.videoviz.repositories.VideoUrlsRepository;
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
    private VideoFileService videoFileService;

    @Autowired
    private VideoUrlsRepository videoUrlsRepository;

    @Autowired
    private VideoJDBCRepository jdbcRepository;

    private ExecutorService executorService;
    private List<Integer> fila = new ArrayList<>();

    private static final Logger log = LoggerFactory.getLogger(VideoDownloadService.class);

    public void addToQueue(DownloadQueue queue) {
        log.info("Tentando adicionar... " + queue.getId());
        if (!fila.contains(queue.getId())) {
            StoreVideoTask task = new StoreVideoTask(queue, new OnStoreResult() {
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
                public void onReadyToFactoryImage(File mp4File, DownloadQueue queue) {
                    try {
                        videoFileService.createPreviewImage(mp4File);
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
                    String md5Sum = Utils.getMD5SUM(file);
                    DownloadQueue tmp = downloadQueueRepository.findById(queue.getId()).get();
                    tmp.setProgress(90);
                    tmp.setSituacao("Gravando registro");
                    log.info(queue.getId() + ": Gravando Registro");
                    downloadQueueRepository.saveAndFlush(tmp);
                    if (jdbcRepository.getByMD5(md5Sum).isEmpty()) {
                        tmp.setProgress(95);
                        tmp.setSituacao("Buscando metadata");
                        log.info(queue.getId() + ": Buscando metadata");
                        downloadQueueRepository.saveAndFlush(tmp);

                        try {
                            VideoModel videoModel = videoRepository.findById(downloadQueue.getIdVideo()).get();
                            ScrapResult result = null;
                            try {
                                result = Scrapper.getScrapResult(downloadQueue.getPageUrl());
                            } catch (VideoNotFoundException | PageNotFoundException | IOException e) {
                                e.printStackTrace();
                            }

                            if (result != null && !result.getTitle().isEmpty()) {
                                videoModel.setTitle(result.getTitle());
                                String tags = "";
                                if (result.getTags() != null && !result.getTags().isEmpty()) {
                                    String tagsStr = "";
                                    for (String tag : result.getTags()) {
                                        tagsStr = tagsStr + "," + tag;
                                    }
                                    tags = (tagsStr.substring(1));
                                }
                                videoModel.setOriginalTags(tags);
                            } else {
                                videoModel.setTitle("Desconhecido");
                            }
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


                    } else {
                        tmp.setProgress(100);
                        tmp.setInProgress(0);
                        tmp.setFailed(1);
                        tmp.setFinished(1);
                        log.info(queue.getId() + ": Video já existe");
                        tmp.setSituacao("Video já existe");
                        file.delete();
                        downloadQueueRepository.saveAndFlush(tmp);
                    }
                    fila.removeIf(id -> id.equals(downloadQueue.getId()));
                }
            });
            fila.add(queue.getId());
            if (executorService == null) {
                executorService = Executors.newFixedThreadPool(Constants.MAX_THREADS);
            }

            log.info("O id: " + queue.getId() + " foi adicionado na fila");
            executorService.submit(task);
        } else {
            log.info("O id já esta na fila " + queue.getId());
        }


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
