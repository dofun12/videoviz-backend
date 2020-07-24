package org.lemanoman.videoviz.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "videoUrls", schema = "advancedvideomanager")
public class VideoUrlsModel  implements Serializable {

    @Id
    @Column(name = "idVideo")
    private Integer idVideo;

    @Column(name = "pageUrl")
    private String pageUrl;

    @Column(name = "midiaUrl")
    private String midiaUrl;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idVideo")
    private VideoModel video;

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

    public String getMidiaUrl() {
        return midiaUrl;
    }

    public void setMidiaUrl(String midiaUrl) {
        this.midiaUrl = midiaUrl;
    }

    public VideoModel getVideo() {
        return video;
    }

    public void setVideo(VideoModel video) {
        this.video = video;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VideoUrlsModel)) return false;
        VideoUrlsModel that = (VideoUrlsModel) o;
        return Objects.equals(idVideo, that.idVideo) &&
                Objects.equals(pageUrl, that.pageUrl) &&
                Objects.equals(midiaUrl, that.midiaUrl);
    }

    @Override
    public int hashCode() {

        return Objects.hash(idVideo, pageUrl, midiaUrl, video);
    }
}
