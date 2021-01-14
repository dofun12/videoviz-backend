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
    private CheckupService checkupService;

    @Autowired
    private MD5FillerService md5FillerService;

    @Autowired
    private VideoDownloadService videoDownloadService;

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Scheduled(initialDelay = 120000, fixedRate = 320000)
    public void runCheckup() {
        checkupService.runCheckoutIFPending();
    }

    @Scheduled(initialDelay = 60000, fixedRate = 300000)
    public void verifyQueue() {
        log.info("Iniciando serviço: verifyQueue");
        if (verifyQueue) {
            log.info("Adicionando na fila...");
            videoDownloadService.updateQueue();
        }

    }

}