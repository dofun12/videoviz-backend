package org.lemanoman.videoviz.dto;

import java.io.Serializable;
import java.sql.Timestamp;

public class VideoJS implements Serializable {
    private Integer idVideo;
    private String title;
    private String code;
    private String imageUrl;
    private String pageUrl;
    private String mediaUrl;
    private String md5Sum;
    private Integer backupok;
    private String location;
    private Integer favorite;
    private Integer rating;
    private Integer isdeleted;
    private String videoSize;
    private String originalTags;
    private Timestamp dateAdded;
    private String dateAddedFormated;
    private String lastWatchedFormated;
    private Integer isfileexist;
    private Integer invalid;
    private Timestamp lastwatched;
    private Double duration;
    private Integer totalWatched;

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setMd5Sum(String md5Sum) {
        this.md5Sum = md5Sum;
    }

    public Integer getBackupok() {
        return backupok;
    }

    public void setBackupok(Integer backupok) {
        this.backupok = backupok;
    }

    public String getLocation() {
        return location;
    }

    public String getDateAddedFormated() {
        return dateAddedFormated;
    }

    public void setDateAddedFormated(String dateAddedFormated) {
        this.dateAddedFormated = dateAddedFormated;
    }

    public String getLastWatchedFormated() {
        return lastWatchedFormated;
    }

    public void setLastWatchedFormated(String lastWatchedFormated) {
        this.lastWatchedFormated = lastWatchedFormated;
    }

    public void setLocation(String location) {
        this.location = location;
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

    public String getOriginalTags() {
        return originalTags;
    }

    public void setOriginalTags(String originalTags) {
        this.originalTags = originalTags;
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

    public Integer getTotalWatched() {
        return totalWatched;
    }

    public void setTotalWatched(Integer totalWatched) {
        this.totalWatched = totalWatched;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }


}
