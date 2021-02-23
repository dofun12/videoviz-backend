package org.lemanoman.videoviz.test;

import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lemanoman.videoviz.Utils;
import org.lemanoman.videoviz.model.DownloadQueue;
import org.lemanoman.videoviz.model.LocationModel;
import org.lemanoman.videoviz.model.VideoModel;
import org.lemanoman.videoviz.repositories.DownloadQueueRepository;
import org.lemanoman.videoviz.repositories.LocationRepository;
import org.lemanoman.videoviz.repositories.VideoRepository;
import org.lemanoman.videoviz.service.VideoDownloadService;
import org.lemanoman.videoviz.service.VideoFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VideoDownloadServiceTest {
    @LocalServerPort
    private int port;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    VideoFileService videoFileService;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    VideoDownloadService videoDownloadService;

    @Autowired
    DownloadQueueRepository downloadQueueRepository;

    @Test
    public void testAddToQueueDefaut(){
        DownloadQueue downloadQueue = new DownloadQueue();
        downloadQueue.setIdVideo(null);
        downloadQueue.setPageUrl("http://localhost/teste/"+ Utils.getRandomString());
        downloadQueue.setSituacao("Aguardando");
        downloadQueue.setVideoUrl("https://f.hubspotusercontent10.net/hubfs/5879415/Faculdade%20Eniac%20-%20Inova%C3%A7%C3%A3o%20-%20Flix-1.mp4");
        downloadQueue.setCode(null);
        downloadQueue.setIdLocation(3);
        downloadQueueRepository.saveAndFlush(downloadQueue);
        videoDownloadService.getStoreVideoTask(downloadQueue,locationRepository.findById(3)).run();
    }
}
