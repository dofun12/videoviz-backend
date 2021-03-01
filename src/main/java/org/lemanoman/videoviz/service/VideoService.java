package org.lemanoman.videoviz.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.lemanoman.videoviz.Resposta;
import org.lemanoman.videoviz.Utils;
import org.lemanoman.videoviz.dto.Duplicated;
import org.lemanoman.videoviz.model.CheckupModel;
import org.lemanoman.videoviz.model.LocationModel;
import org.lemanoman.videoviz.model.VideoModel;
import org.lemanoman.videoviz.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private TagService tagService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private VideoFileService videoFileService;

    public ObjectNode getVideoInfo(VideoModel videoModel) {
        if (videoModel == null) {
            return null; //new Resposta().failed("Video not found on db");
        }
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode videoNode = mapper.createObjectNode();
        videoNode.put("code", videoModel.getCode());
        videoNode.put("idVideo", videoModel.getIdVideo());
        videoNode.put("md5sum", videoModel.getMd5Sum());
        videoNode.put("favorite", videoModel.getFavorite());
        videoNode.put("title", videoModel.getTitle());
        videoNode.put("rating", videoModel.getRating());
        if (videoModel.getVideoUrls() != null) {
            videoNode.put("pageUrl", videoModel.getVideoUrls().getPageUrl());
        }
        videoNode.put("tags", tagService.getTagsByIdVideo(videoModel.getIdVideo()));

        videoNode.put("totalWatched", videoModel.getTotalWatched());
        videoNode.put("original_tags", videoModel.getOriginalTags());
        videoNode.put("favorite", videoModel.getFavorite());
        videoNode.put("idLocation", videoModel.getIdLocation());
        if (videoModel.getDateAdded() != null) {
            videoNode.put("dateAdded", videoModel.getDateAdded().getTime());
        }


        String basePath = null;
        LocationModel locationModel = locationRepository.findById(videoModel.getIdLocation()).orElse(null);
        if (locationModel != null && locationModel.getPath() != null) {
            basePath = locationModel.getPath();
            videoNode.put("image_link", getRemoteLink(locationModel,videoModel,"image"));
            videoNode.put("video_link", getRemoteLink(locationModel,videoModel,"video"));
        }
        videoNode.put("basePath", basePath);

        File mp4File = videoFileService.getFileByVideoModel(locationRepository, videoModel);
        if (mp4File == null || !mp4File.exists()) {
            videoNode.put("fileexists", false);
            return videoNode;
        }
        videoNode.put("fileexists", true);
        ObjectNode fileNode = mapper.createObjectNode();
        fileNode.put("file_size", mp4File.length());
        fileNode.put("filename", mp4File.getName());
        fileNode.put("path", mp4File.getAbsolutePath());
        fileNode.put("actual_md5Sum", Utils.getMD5SumJava(mp4File));
        videoNode.set("file", fileNode);
        return videoNode;
    }

    public String getRemoteLink(LocationModel locationModel,VideoModel videoModel,String resource){
        try {
            UriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/media")
                    .path("/")
                    .path(resource)
                    .path("/")
                    .path(locationModel.getContext())
                    .path("/")
                    .path(videoModel.getCode());
            if("image".equals(resource)){
                return builder.queryParam("time", new Date().getTime() / 1000)
                        .toUriString();
            }
            return builder.toUriString();
        }catch (Exception ex){
            return "No link available";
        }
    }

    public Resposta getFileInfo(VideoModel videoModel) {
        ObjectNode videoNode = getVideoInfo(videoModel);
        if (videoNode == null) {
            return new Resposta().failed("Video not found on db");
        }
        return new Resposta(videoNode).success();
    }

}
