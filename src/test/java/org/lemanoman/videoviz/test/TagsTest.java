package org.lemanoman.videoviz.test;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lemanoman.videoviz.repositories.TagJDBCRepository;
import org.lemanoman.videoviz.repositories.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TagsTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagJDBCRepository tagJDBCRepository;


    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void listAllExistent() throws Exception {
        ArrayNode node = tagJDBCRepository.allTagsExistent();
        Assert.assertTrue(node.size()>3000);
    }

}
