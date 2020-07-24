package org.lemanoman.videoviz.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lemanoman.videoviz.model.DownloadQueue;
import org.lemanoman.videoviz.repositories.DownloadQueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DownloadQueueTest {
    @LocalServerPort
    private int port;

    @Autowired
    private DownloadQueueRepository downloadQueueRepository;


    @Test
    public void testListTop10(){
        try {
            List<DownloadQueue> downloadQueueList = downloadQueueRepository.findTop10ByFinishedAndInProgress(0,0);
            Assert.assertTrue(downloadQueueList!=null);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Test
    public void testList(){
        try {
            List<DownloadQueue> downloadQueueList = downloadQueueRepository.findByFinishedAndInProgress(0,1);
            Assert.assertTrue(downloadQueueList!=null);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

}
