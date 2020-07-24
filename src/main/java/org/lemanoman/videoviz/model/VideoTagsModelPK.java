package org.lemanoman.videoviz.model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

public class VideoTagsModelPK implements Serializable {

    @Id
    @Column(name = "idTag")
    private Integer idTag;

    @Id
    @Column(name = "idVideo")
    private Integer idVideo;

    public VideoTagsModelPK(){

    }

    public VideoTagsModelPK(int idTag, int idVideo) {
        this.idTag = idTag;
        this.idVideo = idVideo;
    }


    public int getIdTag() {
        return idTag;
    }

    public void setIdTag(int idTag) {
        this.idTag = idTag;
    }


    public int getIdVideo() {
        return idVideo;
    }

    public void setIdVideo(int idVideo) {
        this.idVideo = idVideo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoTagsModelPK that = (VideoTagsModelPK) o;
        return idTag == that.idTag &&
                idVideo == that.idVideo;
    }

    @Override
    public int hashCode() {

        return Objects.hash(idTag, idVideo);
    }
}
