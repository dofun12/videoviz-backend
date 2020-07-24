package org.lemanoman.videoviz.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "playlist", schema = "advancedvideomanager", catalog = "")
public class PlaylistModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPlaylist;

    @Column
    private String name;

    public Integer getIdPlaylist() {
        return idPlaylist;
    }

    public void setIdPlaylist(Integer idPlaylist) {
        this.idPlaylist = idPlaylist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlaylistModel)) return false;

        PlaylistModel that = (PlaylistModel) o;

        return idPlaylist != null ? idPlaylist.equals(that.idPlaylist) : that.idPlaylist == null;
    }

    @Override
    public int hashCode() {
        return idPlaylist != null ? idPlaylist.hashCode() : 0;
    }
}
