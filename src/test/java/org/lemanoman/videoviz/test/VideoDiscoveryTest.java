package org.lemanoman.videoviz.test;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.lemanoman.videoviz.Utils;
import org.lemanoman.videoviz.controller.VideoController;
import org.lemanoman.videoviz.model.LocationModel;
import org.lemanoman.videoviz.model.VideoModel;
import org.lemanoman.videoviz.repositories.LocationRepository;
import org.lemanoman.videoviz.repositories.TagRepository;
import org.lemanoman.videoviz.repositories.VideoJDBCRepository;
import org.lemanoman.videoviz.repositories.VideoRepository;
import org.lemanoman.videoviz.service.VideoFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VideoDiscoveryTest {
    @LocalServerPort
    private int port;

    @Autowired
    private VideoJDBCRepository repository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    //@ClassRule
    //public static GenericContainer getContainerMysql(){
    //        return ContainerUtils.MYSQL_CONTAINER;
    //}

    //@Test
    public void discovery() throws Exception {
        LocationModel locationModel = locationRepository.findByContext("/disk").orElse(null);
        if (locationModel == null) {
            locationModel = new LocationModel();
            locationModel.setPath("E:\\WinFiles");
            locationModel.setContext("disk");
            locationRepository.saveAndFlush(locationModel);
        }


        File dir = new File(locationModel.getPath() + "/mp4");
        Assertions.assertTrue(dir.exists());

        int i = 0;
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (!file.getName().endsWith(".mp4")) {
                continue;
            }
            String md5 = Utils.getMD5SumJava(file);

            if (md5 == null) continue;
            VideoModel vm = videoRepository.getByMd5Sum(md5);
            if (vm == null) continue;
            vm.setInvalid(0);
            vm.setLocation(locationModel.getContext());
            vm.setIdLocation(locationModel.getIdLocation());
            vm.setIsfileexist(1);
            videoRepository.save(vm);
            System.out.println("Saving... "+vm.getTitle());
            if(i>=30){
                i=0;
                System.out.println("Flushing...");
                videoRepository.flush();
            }
            i++;
        }
        videoRepository.flush();

        ArrayNode node = repository.listVideo("all", 100, 0);
        Assert.assertEquals(100, node.size());
    }


}
