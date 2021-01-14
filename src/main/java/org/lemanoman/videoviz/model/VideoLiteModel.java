package org.lemanoman.videoviz.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "video")
public class VideoLiteModel implements Serializable {
    public VideoLiteModel() {
    }

    public VideoLiteModel(Integer idVideo) {
        this.idVideo = idVideo;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "idVideo")
    private Integer idVideo;

    @Column(name = "title")
    private String title;

    @Column(name = "code")
    private String code;

    @Column(name = "idLocation")
    private Integer idLocation;

    @Column(name = "isfileexist")
    private Integer isfileexist;

    @Column(name = "invalid")
    private Integer invalid;

    public Integer getIdVideo() {
        return idVideo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setIdVideo(Integer idVideo) {
        this.idVideo = idVideo;
    }

    public Integer getIdLocation() {
        return idLocation;
    }

    public Integer getIsfileexist() {
        return isfileexist;
    }

    public void setIsfileexist(Integer isfileexist) {
        this.isfileexist = isfileexist;
    }

    public Integer getInvalid() {
        return invalid;
    }

    public void setInvalid(Integer invalid) {
        this.invalid = invalid;
    }

    public void setIdLocation(Integer idLocation) {
        this.idLocation = idLocation;
    }
}
