package org.lemanoman.videoviz.service;

import org.lemanoman.videoviz.model.*;
import org.lemanoman.videoviz.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class TagService {
    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoTagsRepository videoTagsRepository;

    @Autowired
    private TagRepository tagRepository;

    public String getTagsByIdVideo(Integer idVideo){
        List<VideoTagsModel> videoTagsModels = videoTagsRepository.findByIdVideo(idVideo);
        List<Integer> idTagsList = videoTagsModels.stream().filter(vt -> vt!=null && vt.getIdTag()>0).map(VideoTagsModel::getIdTag).collect(Collectors.toList());
        List<TagsModel> tags = tagRepository.findByIdTagIn(idTagsList);
        return tags.stream().map(TagsModel::getTag).reduce((s, s2) -> s = s2+","+s).orElse(null);
    }

}
