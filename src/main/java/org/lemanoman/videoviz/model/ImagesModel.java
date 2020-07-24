package org.lemanoman.videoviz.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "images", schema = "advancedvideomanager")
public class ImagesModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "md5sum")
    private String md5sum;

    @Column(name = "path")
    private String path;

    @Column(name = "idVideo")
    private Integer idVideo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMd5sum() {
        return md5sum;
    }

    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getIdVideo() {
        return idVideo;
    }

    public void setIdVideo(Integer idVideo) {
        this.idVideo = idVideo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImagesModel)) return false;
        ImagesModel that = (ImagesModel) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(md5sum, that.md5sum) &&
                Objects.equals(path, that.path) &&
                Objects.equals(idVideo, that.idVideo);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, md5sum, path, idVideo);
    }
}
