package org.lemanoman.videoviz.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "videoHistory", schema = "advancedvideomanager", catalog = "")
public class VideoHistoryModel  implements Serializable {
    private int id;
    private Integer idVideo;
    private Timestamp watched;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "idVideo")
    public Integer getIdVideo() {
        return idVideo;
    }

    public void setIdVideo(Integer idVideo) {
        this.idVideo = idVideo;
    }

    @Basic
    @Column(name = "watched")
    public Timestamp getWatched() {
        return watched;
    }

    public void setWatched(Timestamp watched) {
        this.watched = watched;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoHistoryModel that = (VideoHistoryModel) o;
        return id == that.id &&
                Objects.equals(idVideo, that.idVideo) &&
                Objects.equals(watched, that.watched);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, idVideo, watched);
    }
}
