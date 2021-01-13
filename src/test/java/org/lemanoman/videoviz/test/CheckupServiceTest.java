package org.lemanoman.videoviz.test;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.lemanoman.videoviz.Utils;
import org.lemanoman.videoviz.model.LocationModel;
import org.lemanoman.videoviz.model.VideoModel;
import org.lemanoman.videoviz.repositories.LocationRepository;
import org.lemanoman.videoviz.repositories.VideoJDBCRepository;
import org.lemanoman.videoviz.repositories.VideoRepository;
import org.lemanoman.videoviz.service.CheckupService;
import org.lemanoman.videoviz.service.VerifyVideoFastTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.util.Objects;

@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CheckupServiceTest {
    @LocalServerPort
    private int port;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    private CheckupService checkupService;

    //@ClassRule
    //public static GenericContainer getContainerMysql(){
    //        return ContainerUtils.MYSQL_CONTAINER;
    //}

    @Test
    public void defaultCheckup() {
        checkupService.requestACheckup();
        checkupService.runCheckoutIFPending();
    }

    @Test
    public void testVerify() {
        new VerifyVideoFastTask(videoRepository,locationRepository).run();
    }


}
