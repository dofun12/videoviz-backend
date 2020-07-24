package org.lemanoman.videoviz.model;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "video", schema = "advancedvideomanager")
public class VideoModel implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "idVideo")
    private Integer idVideo;

    @Column(name = "title")
    private String title;

    @Column(name = "code")
    private String code;

    @Column(name = "md5sum")
    private String md5Sum;

    @Column(name = "backupok")
    private Integer backupok;

    @Column(name = "location")
    private String location;

    @Column(name = "favorite")
    private Integer favorite;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "isdeleted")
    private Integer isdeleted;

    @Column(name = "video_size")
    private String videoSize;

    @Column(name = "original_tags")
    private String originalTags;

    @Column(name = "dateAdded")
    private Timestamp dateAdded;

    @Column(name = "isfileexist")
    private Integer isfileexist;

    @Column(name = "invalid")
    private Integer invalid;

    @Column(name = "lastwatched")
    private Timestamp lastwatched;

    @Column(name = "duration")
    private Double duration;

    @Column(name = "totalWatched")
    private Integer totalWatched;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idVideo")
    private VideoUrlsModel videoUrls;



    public Integer getIdVideo() {
        return idVideo;
    }

    public void setIdVideo(Integer idVideo) {
        this.idVideo = idVideo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMd5Sum() {
        return md5Sum;
    }

    public void setMd5Sum(String md5Sum) {
        this.md5Sum = md5Sum;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VideoModel that = (VideoModel) o;
        return idVideo == that.idVideo &&
                Objects.equals(title, that.title) &&
                Objects.equals(code, that.code) &&
                Objects.equals(md5Sum, that.md5Sum) &&
                Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {

        return Objects.hash(idVideo, title, code, md5Sum, location);
    }

    public VideoUrlsModel getVideoUrls() {
        return videoUrls;
    }

    public void setVideoUrls(VideoUrlsModel videoUrls) {
        this.videoUrls = videoUrls;
    }

    public Integer getFavorite() {
        return favorite;
    }

    public void setFavorite(Integer favorite) {
        this.favorite = favorite;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getIsdeleted() {
        return isdeleted;
    }

    public void setIsdeleted(Integer isdeleted) {
        this.isdeleted = isdeleted;
    }

    public String getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(String videoSize) {
        this.videoSize = videoSize;
    }

    public Timestamp getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Timestamp dateAdded) {
        this.dateAdded = dateAdded;
    }

    public Integer getIsfileexist() {
        return isfileexist;
    }

    public void setIsfileexist(Integer isfileexist) {
        this.isfileexist = isfileexist;
    }

    public Integer getInvalid() {
        return invalid;
    }

    public void setInvalid(Integer invalid) {
        this.invalid = invalid;
    }

    public Timestamp getLastwatched() {
        return lastwatched;
    }

    public void setLastwatched(Timestamp lastwatched) {
        this.lastwatched = lastwatched;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public String getOriginalTags() {
        return originalTags;
    }

    public void setOriginalTags(String originalTags) {
        this.originalTags = originalTags;
    }

    public Integer getBackupok() {
        return backupok;
    }

    public void setBackupok(Integer backupok) {
        this.backupok = backupok;
    }

    public Integer getTotalWatched() {
        return totalWatched;
    }



    public void setTotalWatched(Integer totalWatched) {
        this.totalWatched = totalWatched;
    }
}
