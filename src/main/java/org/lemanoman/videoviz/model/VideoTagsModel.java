package org.lemanoman.videoviz.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "videoTags", schema = "advancedvideomanager", catalog = "")
@IdClass(VideoTagsModelPK.class)
public class VideoTagsModel  implements Serializable {
    @Id
    @Column(name = "idTag")
    private Integer idTag;

    @Id
    @Column(name = "idVideo")
    private Integer idVideo;


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
        VideoTagsModel that = (VideoTagsModel) o;
        return idTag == that.idTag &&
                idVideo == that.idVideo;
    }

    @Override
    public int hashCode() {

        return Objects.hash(idTag, idVideo);
    }

}
