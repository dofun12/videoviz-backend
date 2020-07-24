package org.lemanoman.videoviz.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "videoPlaylist", schema = "advancedvideomanager", catalog = "")
@IdClass(VideoPlaylistPK.class)
public class VideoPlaylistModel implements Serializable {
    @Id
    @Column(name = "idPlaylist")
    private Integer idPlaylist;

    @Id
    @Column(name = "idVideo")
    private Integer idVideo;

    @Column(name = "dateAdded")
    private Date dateAdded;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VideoPlaylistModel)) return false;

        VideoPlaylistModel that = (VideoPlaylistModel) o;

        if (idPlaylist != that.idPlaylist) return false;
        return idVideo == that.idVideo;
    }

    @Override
    public int hashCode() {
        int result = idPlaylist;
        result = 31 * result + idVideo;
        return result;
    }

    public int getIdPlaylist() {
        return idPlaylist;
    }

    public void setIdPlaylist(int idPlaylist) {
        this.idPlaylist = idPlaylist;
    }

    public int getIdVideo() {
        return idVideo;
    }

    public void setIdVideo(int idVideo) {
        this.idVideo = idVideo;
    }

    public void setIdPlaylist(Integer idPlaylist) {
        this.idPlaylist = idPlaylist;
    }

    public void setIdVideo(Integer idVideo) {
        this.idVideo = idVideo;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }
}
