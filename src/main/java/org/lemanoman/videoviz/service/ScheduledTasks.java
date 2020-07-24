package org.lemanoman.videoviz.service;

import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
    @Value("${service.runMD5Service}")
    private Boolean runMD5Service;

    @Value("${service.verifyQueue}")
    private Boolean verifyQueue;

    @Autowired
    private MD5FillerService md5FillerService;

    @Autowired
    private VideoDownloadService videoDownloadService;

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(initialDelay = 120000, fixedRate = 300000)
    public void runMD5Service() {
        log.info("Iniciando serviço: runMD5Service");
        if (runMD5Service) {
            log.info("Runing fillMD5");
            md5FillerService.fillMD5();
        }else{
            log.info("Serviço runMD5Service desativado");
        }
    }

    @Scheduled(initialDelay = 60000, fixedRate = 300000)
    public void verifyQueue() {
        log.info("Iniciando serviço: verifyQueue");
        if (verifyQueue) {
            log.info("Adicionando na fila...");
            log.info("Ta vazio, então bora por pa dentro");
            videoDownloadService.updateQueue();
        }else{
            log.info("Serviço verifyQueue desativado");
        }

    }

}