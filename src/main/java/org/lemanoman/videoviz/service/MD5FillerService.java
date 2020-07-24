package org.lemanoman.videoviz.service;

import org.lemanoman.videoviz.Constants;
import org.lemanoman.videoviz.Utils;
import org.lemanoman.videoviz.model.VideoModel;
import org.lemanoman.videoviz.repositories.VideoJDBCRepository;
import org.lemanoman.videoviz.repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;

@Service
public class MD5FillerService {

    @Autowired
    private VideoJDBCRepository videoJDBCRepository;

    @Autowired
    private VideoRepository videoRepository;

    public void fillMD5(){
        List<Map<String,Object>> lista = videoJDBCRepository.getListMD5();
        for(Map<String,Object> map: lista){
            File mp4File = new File(Constants.MP4_BASE_PATH+ File.separator+map.get("code")+".mp4");
            if(mp4File.exists()){
                VideoModel videoModel = videoRepository.findById((Integer) map.get("idVideo")).get();
                videoModel.setMd5Sum(Utils.getMD5SUM(mp4File));
                videoModel.setIsfileexist(1);
                videoModel.setVideoSize(String.valueOf(mp4File.length()));
                videoRepository.save(videoModel);
            }
        }
    }
}
