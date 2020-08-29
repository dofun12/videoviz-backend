-- MySQL dump 10.13  Distrib 5.7.12, for Linux (x86_64)
--
-- Host: localhost    Database: advancedvideomanager
-- ------------------------------------------------------
-- Server version	5.7.12

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Temporary view structure for view `VW_VIDEOS_DISPONIVEIS`
--

DROP TABLE IF EXISTS `VW_VIDEOS_DISPONIVEIS`;
/*!50001 DROP VIEW IF EXISTS `VW_VIDEOS_DISPONIVEIS`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `VW_VIDEOS_DISPONIVEIS` AS SELECT
                                                    1 AS `totalWatched`,
                                                    1 AS `idVideo`,
                                                    1 AS `title`,
                                                    1 AS `code`,
                                                    1 AS `md5sum`,
                                                    1 AS `location`,
                                                    1 AS `backupok`,
                                                    1 AS `favorite`,
                                                    1 AS `rating`,
                                                    1 AS `isdeleted`,
                                                    1 AS `video_size`,
                                                    1 AS `dateAdded`,
                                                    1 AS `isfileexist`,
                                                    1 AS `lastwatched`,
                                                    1 AS `duration`,
                                                    1 AS `original_tags`,
                                                    1 AS `invalid`,
                                                    1 AS `midiaUrl`,
                                                    1 AS `pageUrl`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `downloadQueue`
--

DROP TABLE IF EXISTS `downloadQueue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `downloadQueue` (
                                 `id` int(11) NOT NULL AUTO_INCREMENT,
                                 `idVideo` int(11) DEFAULT NULL,
                                 `title` text,
                                 `pageUrl` text,
                                 `videoUrl` text,
                                 `fileOrigin` text,
                                 `code` varchar(20) DEFAULT NULL,
                                 `progress` int(11) DEFAULT NULL,
                                 `situacao` varchar(255) DEFAULT NULL,
                                 `videoSize` mediumtext,
                                 `dateAdded` datetime DEFAULT NULL,
                                 `inProgress` tinyint(4) DEFAULT '0',
                                 `finished` tinyint(4) DEFAULT '0',
                                 `failed` tinyint(4) DEFAULT '0',
                                 `idLocation` int(11) NOT NULL,
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `idVideo` (`idVideo`)
) ENGINE=InnoDB AUTO_INCREMENT=6253 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `images`
--

DROP TABLE IF EXISTS `images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `images` (
                          `id` int(11) DEFAULT NULL,
                          `md5sum` tinytext,
                          `path` tinytext,
                          `idVideo` int(11) DEFAULT NULL,
                          `id_video` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `location`
--

DROP TABLE IF EXISTS `location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `location` (
                            `idLocation` int(11) NOT NULL AUTO_INCREMENT,
                            `path` text,
                            `context` varchar(200) DEFAULT NULL,
                            PRIMARY KEY (`idLocation`),
                            UNIQUE KEY `context` (`context`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `playlist`
--

DROP TABLE IF EXISTS `playlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `playlist` (
                            `idPlaylist` int(11) NOT NULL AUTO_INCREMENT,
                            `name` tinytext,
                            PRIMARY KEY (`idPlaylist`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `tags`
--

DROP TABLE IF EXISTS `tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tags` (
                        `idTag` int(11) DEFAULT NULL,
                        `tag` tinytext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usuario` (
                           `id` int(11) NOT NULL AUTO_INCREMENT,
                           `login` varchar(255) DEFAULT NULL,
                           `password` varchar(100) DEFAULT NULL,
                           `role` varchar(255) DEFAULT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `video`
--

DROP TABLE IF EXISTS `video`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `video` (
                         `idVideo` int(11) NOT NULL AUTO_INCREMENT,
                         `title` varchar(255) DEFAULT NULL,
                         `code` varchar(20) DEFAULT NULL,
                         `md5sum` varchar(35) DEFAULT NULL,
                         `location` varchar(20) DEFAULT 'video',
                         `backupok` int(11) DEFAULT NULL,
                         `favorite` tinyint(4) DEFAULT '0',
                         `rating` int(11) DEFAULT '0',
                         `isdeleted` tinyint(4) DEFAULT '0',
                         `video_size` mediumtext,
                         `dateAdded` datetime DEFAULT NULL,
                         `isfileexist` tinyint(4) DEFAULT '1',
                         `lastwatched` datetime DEFAULT NULL,
                         `duration` double DEFAULT NULL,
                         `original_tags` text,
                         `invalid` int(11) DEFAULT '0',
                         `totalWatched` int(11) DEFAULT NULL,
                         `idLocation` int(11) NOT NULL,
                         PRIMARY KEY (`idVideo`),
                         UNIQUE KEY `code` (`code`),
                         KEY `index_video` (`rating`,`isdeleted`,`isfileexist`,`invalid`)
) ENGINE=InnoDB AUTO_INCREMENT=47430 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `videoHistory`
--

DROP TABLE IF EXISTS `videoHistory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `videoHistory` (
                                `id` int(11) DEFAULT NULL,
                                `idVideo` int(11) DEFAULT NULL,
                                `watched` timestamp NULL DEFAULT NULL,
                                KEY `videoHistory_idVideo_index` (`idVideo`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `videoMirrors`
--

DROP TABLE IF EXISTS `videoMirrors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `videoMirrors` (
                                `id` int(11) DEFAULT NULL,
                                `idVideo` int(11) DEFAULT NULL,
                                `pageUrl` mediumtext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `videoPlaylist`
--

DROP TABLE IF EXISTS `videoPlaylist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `videoPlaylist` (
                                 `idPlaylist` int(11) NOT NULL,
                                 `idVideo` int(11) NOT NULL,
                                 `dateAdded` datetime DEFAULT NULL,
                                 PRIMARY KEY (`idPlaylist`,`idVideo`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `videoTags`
--

DROP TABLE IF EXISTS `videoTags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `videoTags` (
                             `idTag` int(11) DEFAULT NULL,
                             `idVideo` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `videoUrls`
--

DROP TABLE IF EXISTS `videoUrls`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `videoUrls` (
                             `idVideo` int(11) DEFAULT NULL,
                             `pageUrl` mediumtext,
                             `midiaUrl` mediumtext
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Final view structure for view `VW_VIDEOS_DISPONIVEIS`
--

/*!50001 DROP VIEW IF EXISTS `VW_VIDEOS_DISPONIVEIS`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
    /*!50013 DEFINER=`magic`@`%` SQL SECURITY DEFINER */
    /*!50001 VIEW `VW_VIDEOS_DISPONIVEIS` AS (select (select count(0) from `videoHistory` `vh` where (`vh`.`idVideo` = `v`.`idVideo`)) AS `totalWatched`,`v`.`idVideo` AS `idVideo`,`v`.`title` AS `title`,`v`.`code` AS `code`,`v`.`md5sum` AS `md5sum`,`v`.`location` AS `location`,`v`.`backupok` AS `backupok`,`v`.`favorite` AS `favorite`,`v`.`rating` AS `rating`,`v`.`isdeleted` AS `isdeleted`,`v`.`video_size` AS `video_size`,`v`.`dateAdded` AS `dateAdded`,`v`.`isfileexist` AS `isfileexist`,`v`.`lastwatched` AS `lastwatched`,`v`.`duration` AS `duration`,`v`.`original_tags` AS `original_tags`,`v`.`invalid` AS `invalid`,`vu`.`midiaUrl` AS `midiaUrl`,`vu`.`pageUrl` AS `pageUrl` from (`video` `v` left join `videoUrls` `vu` on((`vu`.`idVideo` = `v`.`idVideo`))) where ((`v`.`isdeleted` = 0) and ((`v`.`isdeleted` = 0) or isnull(`v`.`isdeleted`)) and (`v`.`isfileexist` = 1) and (`v`.`invalid` = 0))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-08-29 15:51:12

insert into usuario (login, password, role) values ('admin','$2a$10$a8MgCZTN1Qjcyf1NBnj1DOzaDisSTG4wIotrbJXaEmbXidXA8yK/q','admin');
insert into location(context, path) values ('teste', 'E:\\data\\teste');