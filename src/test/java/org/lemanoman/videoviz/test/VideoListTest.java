package org.lemanoman.videoviz.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.lemanoman.videoviz.controller.VideoController;
import org.lemanoman.videoviz.repositories.TagRepository;
import org.lemanoman.videoviz.repositories.VideoJDBCRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VideoListTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private VideoController controller;

    @Autowired
    private VideoJDBCRepository repository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void listarTodos() throws Exception {
        ArrayNode node = repository.listVideo("all",100,0);
        Assert.assertEquals(100,node.size());
    }

    @Test
    public void listarRecents() throws Exception {
        ArrayNode node = repository.listVideo("recents",100,0);
        Assert.assertEquals(100,node.size());
    }


    @Test
    public void listarUnwatched() throws Exception {
        try {
            ArrayNode node = repository.listVideo("unwatched",100,0);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        //Assert.assertEquals(100,node.size());

    }

    @Test
    public void listarWatched() throws Exception {
        ArrayNode node = repository.listVideo("watched",100,0);
        Assert.assertEquals(100,node.size());
    }

    @Test
    public void listarAleatorios() throws Exception {
        ArrayNode node = repository.listVideo("random",100,0);
        Assert.assertEquals(100,node.size());
    }

    @Test
    public void listarFavorites() throws Exception {
        ArrayNode node = repository.listVideo("favorites",100,0);
        Assert.assertEquals(100,node.size());
    }



    @Test
    public void listarNewRand() throws Exception {
        ArrayNode node = repository.listVideo("new-rand",100,0);
        Assert.assertEquals(100,node.size());
    }

    @Test
    public void testHistorico() throws Exception {
        try {
            this.restTemplate.put("http://localhost:" + port + "/api/video/updateHistory/27261",null);
        }catch (Exception ex){
            Assert.fail("Erro ao acessar");
        }

    }

}
