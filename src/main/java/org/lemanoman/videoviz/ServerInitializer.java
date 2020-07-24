package org.lemanoman.videoviz;

import org.lemanoman.videoviz.model.DownloadQueue;
import org.lemanoman.videoviz.repositories.DownloadQueueRepository;
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
    private DownloadQueueRepository downloadQueueRepository;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        if("prod".equals(environment)){
            List<DownloadQueue> downloadQueueList = downloadQueueRepository.findByFinishedAndInProgress(0,1);
            for(DownloadQueue dq:downloadQueueList){
                dq.setInProgress(0);
                downloadQueueRepository.save(dq);
            }
            downloadQueueRepository.flush();
        }
    }
}