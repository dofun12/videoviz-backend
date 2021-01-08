package org.lemanoman.videoviz.test;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.MountableFile;

public class ContainerUtils {

    public static GenericContainer MYSQL_CONTAINER
            = new GenericContainer("mysql:5.7.12")
            .withCopyFileToContainer(MountableFile.forHostPath("E:\\projetos\\videoviz\\docker\\db\\Cvideoviz_2021-01-07-04-14.sql"),"/docker-entrypoint-initdb.d/")
            .withExposedPorts(3306)
            .withEnv("MYSQL_ROOT_PASSWORD","videoviz")
            .withEnv("MYSQL_DATABASE","videoviz")
            .withEnv("MYSQL_USER","videoviz");
}
