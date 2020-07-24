package org.lemanoman.videoviz.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "videoMirrors", schema = "advancedvideomanager")
public class VideoMirrorsModel {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Integer id;

    @Column(name = "idVideo")
    private Integer idVideo;

    @Column(name = "pageUrl")
    private String pageUrl;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdVideo() {
        return idVideo;
    }

    public void setIdVideo(Integer idVideo) {
        this.idVideo = idVideo;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VideoMirrorsModel)) return false;
        VideoMirrorsModel that = (VideoMirrorsModel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
