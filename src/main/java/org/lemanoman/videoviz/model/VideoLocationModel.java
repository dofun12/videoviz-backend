package org.lemanoman.videoviz.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "videoLocation")
@IdClass(VideoLocationModelPK.class)
public class VideoLocationModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "idLocation", nullable = false)
    private Integer idLocation;

    @Id
    @Column(name = "idVideo", nullable = false)
    private Integer idVideo;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getIdLocation() {
        return idLocation;
    }

    public void setIdLocation(Integer idLocation) {
        this.idLocation = idLocation;
    }

    public Integer getIdVideo() {
        return idVideo;
    }

    public void setIdVideo(Integer idVideo) {
        this.idVideo = idVideo;
    }
}