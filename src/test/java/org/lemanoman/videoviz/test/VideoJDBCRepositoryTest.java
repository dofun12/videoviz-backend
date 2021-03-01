package org.lemanoman.videoviz.test;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.lemanoman.videoviz.controller.VideoController;
import org.lemanoman.videoviz.dto.Duplicated;
import org.lemanoman.videoviz.repositories.TagRepository;
import org.lemanoman.videoviz.repositories.VideoJDBCRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VideoJDBCRepositoryTest {
    @LocalServerPort
    private int port;

    @Autowired
    private VideoJDBCRepository videoJDBCRepository;

    @ClassRule
    public static GenericContainer getContainerMysql(){
        return ContainerUtils.MYSQL_CONTAINER;
    }


    @Test
    public void testListDuplicatedMD5() throws Exception {
        try {
            List<Duplicated> list = videoJDBCRepository.getListDuplicates();
            Assertions.assertNotNull(list);
        }catch (Exception ex){
            Assert.fail("Erro ao acessar");
        }
    }

}
