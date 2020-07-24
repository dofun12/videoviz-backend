package org.lemanoman.videoviz.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tags", schema = "advancedvideomanager", catalog = "")
public class TagsModel implements Serializable {
    private Integer idTag;
    private String tag;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idTag")
    public Integer getIdTag() {
        return idTag;
    }

    public void setIdTag(Integer idTag) {
        this.idTag = idTag;
    }

    @Column(name = "tag")
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagsModel tagsModel = (TagsModel) o;
        return idTag == tagsModel.idTag &&
                Objects.equals(tag, tagsModel.tag);
    }

    @Override
    public int hashCode() {

        return Objects.hash(idTag, tag);
    }
}
