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
import org.lemanoman.videoviz.repositories.VideoPageableRepository;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CheckupServiceTest {
    @LocalServerPort
    private int port;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    VideoPageableRepository videoPageableRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    private CheckupService checkupService;

    //@ClassRule
    //public static GenericContainer getContainerMysql(){
    //        return ContainerUtils.MYSQL_CONTAINER;
    //}

    //@Test
    public void defaultCheckup() {
        checkupService.requestACheckup();
        checkupService.runCheckoutIFPending();
    }

    //@Test
    public void testVerify() {
        new VerifyVideoFastTask(videoRepository,locationRepository,videoPageableRepository).run();
    }


    @Test
    public void testVerifyLinks() {
        List<String> lines = readFile("E:\\WinFiles\\links.txt");
        if(lines==null||lines.isEmpty()){
            return;
        }
        List<String> validLinks = checkupService.validadeLinks(lines);
        System.out.println("Valid links: "+validLinks.size()+"/"+lines.size());
    }

    private List<String> readFile(String fileName){
        try {
            List<String> list = new ArrayList<>();

            try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {

                //br returns as stream and convert it into a List
                list = br.lines().collect(Collectors.toList());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return list;
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

}
