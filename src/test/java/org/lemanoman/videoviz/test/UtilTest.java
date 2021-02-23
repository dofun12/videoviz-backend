package org.lemanoman.videoviz.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.lemanoman.videoviz.Utils;
import org.lemanoman.videoviz.repositories.LocationRepository;
import org.lemanoman.videoviz.repositories.VideoPageableRepository;
import org.lemanoman.videoviz.repositories.VideoRepository;
import org.lemanoman.videoviz.service.CheckupService;
import org.lemanoman.videoviz.service.VerifyVideoFastTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@SpringBootTest
public class UtilTest {

    @Test
    public void testRandomString() {
        String randomString = Utils.getRandomString();
        String randomString2 = Utils.getRandomString();

        Assertions.assertNotEquals(randomString,randomString2);
        System.out.println(randomString);
    }

}
