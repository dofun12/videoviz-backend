package org.lemanoman.videoviz.dto;

import org.lemanoman.videoviz.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class Duplicated {
    private Integer total;
    private String md5sum;
    private List<Integer> idVideoList;

    public Duplicated(Map<String, Object> map) {
        this.total = Utils.toInt(map.get("total"));
        this.md5sum = Utils.toStr(map.get("md5sum"));
        this.idVideoList = Utils.toListInt(map.get("idVideos"));
    }



    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getMd5sum() {
        return md5sum;
    }

    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }

    public List<Integer> getIdVideoList() {
        return idVideoList;
    }

    public void setIdVideoList(List<Integer> idVideoList) {
        this.idVideoList = idVideoList;
    }
}
