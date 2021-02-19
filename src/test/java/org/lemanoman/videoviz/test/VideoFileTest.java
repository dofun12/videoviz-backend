package org.lemanoman.videoviz.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.lemanoman.videoviz.model.VideoModel;
import org.lemanoman.videoviz.repositories.LocationRepository;
import org.lemanoman.videoviz.repositories.VideoPageableRepository;
import org.lemanoman.videoviz.repositories.VideoRepository;
import org.lemanoman.videoviz.service.CheckupService;
import org.lemanoman.videoviz.service.VerifyVideoFastTask;
import org.lemanoman.videoviz.service.VideoFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@ActiveProfiles("dev")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VideoFileTest {
    @LocalServerPort
    private int port;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    VideoFileService videoFileService;

    @Autowired
    LocationRepository locationRepository;

    @Test
    void testGetVideoFile(){
        VideoModel vm = videoRepository.findAllByInvalidAndIsfileexist(0,1, PageRequest.of(0, 1)).get(0);
        Assertions.assertNotNull(videoFileService.getFileByVideoModel(locationRepository,vm));
    }
}
