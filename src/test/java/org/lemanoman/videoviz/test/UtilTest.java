package org.lemanoman.videoviz.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lemanoman.videoviz.Utils;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class UtilTest {

    @Test
    public void testRandomString() {
        String randomString = Utils.getRandomName();
        String randomString2 = Utils.getRandomName();

        Assertions.assertNotEquals(randomString,randomString2);
        System.out.println(randomString);
    }

}
