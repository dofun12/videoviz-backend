package org.lemanoman.videoviz.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "tags", schema = "advancedvideomanager", catalog = "")
public class TagsSelectedModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idTag")
    private Integer idTag;
    @Column(name = "tag")
    private String tag;

    @Column
    private Integer selected;

    public Integer getSelected() {
        return selected;
    }

    public void setSelected(Integer selected) {
        this.selected = selected;
    }

    public Integer getIdTag() {
        return idTag;
    }

    public void setIdTag(Integer idTag) {
        this.idTag = idTag;
    }


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
        TagsSelectedModel tagsModel = (TagsSelectedModel) o;
        return idTag == tagsModel.idTag &&
                Objects.equals(tag, tagsModel.tag);
    }

    @Override
    public int hashCode() {

        return Objects.hash(idTag, tag);
    }
}
