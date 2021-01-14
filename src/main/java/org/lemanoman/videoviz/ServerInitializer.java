package org.lemanoman.videoviz;

import org.lemanoman.videoviz.model.DownloadQueue;
import org.lemanoman.videoviz.repositories.DownloadQueueRepository;
import org.lemanoman.videoviz.service.CheckupService;
import org.lemanoman.videoviz.service.ScheduledTasks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ServerInitializer implements ApplicationRunner {
    @Value("${environment}")
    private String environment;

    @Autowired
    private CheckupService checkupService;

    @Autowired
    private DownloadQueueRepository downloadQueueRepository;

    private static final Logger log = LoggerFactory.getLogger(ServerInitializer.class);

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        log.info("Limpando checkups bugados... ");
        checkupService.clean();
        if("prod".equals(environment)){
            log.info("Limpando queue bugadas... ");
            List<DownloadQueue> downloadQueueList = downloadQueueRepository.findByFinishedAndInProgress(0,1);
            for(DownloadQueue dq:downloadQueueList){
                dq.setInProgress(0);
                downloadQueueRepository.save(dq);
            }
            downloadQueueRepository.flush();
        }
    }
}