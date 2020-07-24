package org.lemanoman.videoviz.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lemanoman.videoviz.model.VideoModel;
import org.lemanoman.videoviz.repositories.VideoRepository;
import org.lemanoman.videoviz.service.MD5FillerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MD5FillerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private MD5FillerService md5FillerService;

    @Test
    public void testSave(){
        VideoModel videoModel = videoRepository.findById(35589).get();
        videoModel.setBackupok(1);
        videoRepository.save(videoModel);
    }

    @Test
    public void fillMD5Teste() throws Exception {
        md5FillerService.fillMD5();
    }

}
