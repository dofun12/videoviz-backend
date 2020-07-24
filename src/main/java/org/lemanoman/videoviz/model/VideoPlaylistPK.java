package org.lemanoman.videoviz.model;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class VideoPlaylistPK implements Serializable {
    @Id
    @Column(name = "idPlaylist")
    private int idPlaylist;
    private int idVideo;


    @Column(name = "idVideo")
    @Id
    public int getIdVideo() {
        return idVideo;
    }

    public void setIdVideo(int idVideo) {
        this.idVideo = idVideo;
    }

    public int getIdPlaylist() {
        return idPlaylist;
    }

    public void setIdPlaylist(int idPlaylist) {
        this.idPlaylist = idPlaylist;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VideoPlaylistPK)) return false;

        VideoPlaylistPK that = (VideoPlaylistPK) o;

        if (idPlaylist != that.idPlaylist) return false;
        return idVideo == that.idVideo;
    }

    @Override
    public int hashCode() {
        int result = idPlaylist;
        result = 31 * result + idVideo;
        return result;
    }


}

