package org.lemanoman.videoviz.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "todownload", schema = "advancedvideomanager")
public class TodownloadModel  implements Serializable {
    @Id
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "pageUrl")
    private String pageUrl;

    @Basic
    @Column(name = "added")
    private Timestamp added;

    @Basic
    @Column(name = "status")
    private String status;

    @Basic
    @Column(name = "isonline")
    private Integer isonline;

    @Basic
    @Column(name = "last_verified")
    private Timestamp lastVerified;

    @Basic
    @Column(name = "dateFinished")
    private Timestamp dateFinished;

    @Basic
    @Column(name = "dateAddedToQueue")
    private Timestamp dateAddedToQueue;

    @Basic
    @Column(name = "dateDownloadEnd")
    private Timestamp dateDownloadEnd;

    @Basic
    @Column(name = "collectorname")
    private String collectorname;

    @Basic
    @Column(name = "idVideo")
    private Integer idVideo;

    @Basic
    @Column(name = "finished")
    private Integer finished;

    @Basic
    @Column(name = "expectedsize")
    private Long expectedsize;

    @Basic
    @Column(name = "title")
    private String title;

    @Column(name = "original_tags")
    private String originalTags;

    @Basic
    @Column(name = "iserror")
    private Integer iserror;

    @Basic
    @Column(name = "filename")
    private String filename;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }


    public Timestamp getAdded() {
        return added;
    }

    public void setAdded(Timestamp added) {
        this.added = added;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public Integer getIsonline() {
        return isonline;
    }

    public void setIsonline(Integer isonline) {
        this.isonline = isonline;
    }


    public Timestamp getLastVerified() {
        return lastVerified;
    }

    public void setLastVerified(Timestamp lastVerified) {
        this.lastVerified = lastVerified;
    }

    public Timestamp getDateFinished() {
        return dateFinished;
    }

    public void setDateFinished(Timestamp dateFinished) {
        this.dateFinished = dateFinished;
    }

    public Timestamp getDateAddedToQueue() {
        return dateAddedToQueue;
    }

    public void setDateAddedToQueue(Timestamp dateAddedToQueue) {
        this.dateAddedToQueue = dateAddedToQueue;
    }

    public Timestamp getDateDownloadEnd() {
        return dateDownloadEnd;
    }

    public void setDateDownloadEnd(Timestamp dateDownloadEnd) {
        this.dateDownloadEnd = dateDownloadEnd;
    }

    public String getCollectorname() {
        return collectorname;
    }

    public void setCollectorname(String collectorname) {
        this.collectorname = collectorname;
    }

    public Integer getIdVideo() {
        return idVideo;
    }

    public void setIdVideo(Integer idVideo) {
        this.idVideo = idVideo;
    }

    public Integer getFinished() {
        return finished;
    }

    public void setFinished(Integer finished) {
        this.finished = finished;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TodownloadModel)) return false;
        TodownloadModel that = (TodownloadModel) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    public Long getExpectedsize() {
        return expectedsize;
    }

    public void setExpectedsize(Long expectedsize) {
        this.expectedsize = expectedsize;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getIserror() {
        return iserror;
    }

    public void setIserror(Integer iserror) {
        this.iserror = iserror;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getOriginalTags() {
        return originalTags;
    }

    public void setOriginalTags(String originalTags) {
        this.originalTags = originalTags;
    }
}
