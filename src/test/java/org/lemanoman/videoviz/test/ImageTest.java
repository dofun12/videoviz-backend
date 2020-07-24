package org.lemanoman.videoviz.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lemanoman.videoviz.service.VideoFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ImageTest {
    @LocalServerPort
    private int port;

    @Value("${custom.imagelocation}")
    private String imgLocation;


    @Value("${custom.uploadlocation}")
    private String uploadlocation;


    @Value("${ffmpeg.location}")
    private String ffmpegLocation;

    @Value("classpath:video.mp4")
    Resource resourceFile;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VideoFileService videoFileService;

    @Test
    public void createImage() throws Exception {
        /**
         File mp4 = resourceFile.getFile();
         File imgFile = videoFileService.createPreviewImage(mp4);
         Assert.assertTrue(imgFile.exists());
         imgFile.delete();
         **/
    }

}
