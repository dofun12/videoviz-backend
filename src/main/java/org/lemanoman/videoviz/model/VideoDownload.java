/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lemanoman.videoviz.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * The persistent class for the contact database table.
 */
@Entity
@Table(name = "VideoDownload", schema = "scavanger")
public class VideoDownload implements Serializable {
    @Id
    @Column
    private Integer id;
    @Column
    private String title;

    @Column
    private String htmlFile;

    @Column
    private Integer isdownloaded;

    @Column
    private Integer isdownloading;

    @Column
    private Integer isencrypt;

    @Column
    private String pageUrl;

    @Column
    private String siteTags;

    @Column
    private String midiaUrl;

    @Column
    private Long dateAdded;

    @Column
    private Long dateDownloaded;

    @Column
    private Long lastVerified;

    @Column
    private Integer idToDownload;

    @Column
    private Long dateAddedToQueue;

    @Column
    private Long dateFinishedDownload;

    @Column
    private String status;

    @Column
    private Integer priority;

    @Column
    private Long size;

    @Column
    private Long sizeDownloaded;

    @Column
    private Integer iserror;

    public VideoDownload() {

    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the htmlFile
     */
    public String getHtmlFile() {
        return htmlFile;
    }

    /**
     * @param htmlFile the htmlFile to set
     */
    public void setHtmlFile(String htmlFile) {
        this.htmlFile = htmlFile;
    }


    /**
     * @return the isencrypt
     */
    public Integer getIsencrypt() {
        return isencrypt;
    }

    /**
     * @param isencrypt the isencrypt to set
     */
    public void setIsencrypt(Integer isencrypt) {
        this.isencrypt = isencrypt;
    }

    /**
     * @return the pageUrl
     */
    public String getPageUrl() {
        return pageUrl;
    }

    /**
     * @param pageUrl the pageUrl to set
     */
    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    /**
     * @return the midiaUrl
     */
    public String getMidiaUrl() {
        return midiaUrl;
    }

    /**
     * @param midiaUrl the midiaUrl to set
     */
    public void setMidiaUrl(String midiaUrl) {
        this.midiaUrl = midiaUrl;
    }

    /**
     * @return the dateAdded
     */
    public Long getDateAdded() {
        return dateAdded;
    }

    /**
     * @param dateAdded the dateAdded to set
     */
    public void setDateAdded(Long dateAdded) {
        this.dateAdded = dateAdded;
    }

    /**
     * @return the dateDownloaded
     */
    public Long getDateDownloaded() {
        return dateDownloaded;
    }

    /**
     * @param dateDownloaded the dateDownloaded to set
     */
    public void setDateDownloaded(Long dateDownloaded) {
        this.dateDownloaded = dateDownloaded;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        if(status.length()>255){
            status = status.substring(0,254);
        }
        this.status = status;
    }

    /**
     * @return the priority
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * @return the size
     */
    public Long getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(Long size) {
        this.size = size;
    }

    /**
     * @return the sizeDownloaded
     */
    public Long getSizeDownloaded() {
        return sizeDownloaded;
    }

    /**
     * @param sizeDownloaded the sizeDownloaded to set
     */
    public void setSizeDownloaded(Long sizeDownloaded) {
        this.sizeDownloaded = sizeDownloaded;
    }

    /**
     * @return the iserror
     */
    public Integer getIserror() {
        if (iserror == null) {
            iserror = 0;
        }
        return iserror;
    }

    /**
     * @param iserror the iserror to set
     */
    public void setIserror(Integer iserror) {
        this.iserror = iserror;
    }

    /**
     * @return the isdownloaded
     */
    public Integer getIsdownloaded() {
        return isdownloaded;
    }

    /**
     * @param isdownloaded the isdownloaded to set
     */
    public void setIsdownloaded(Integer isdownloaded) {
        this.isdownloaded = isdownloaded;
    }

    /**
     * @return the isdownloading
     */
    public Integer getIsdownloading() {
        if (isdownloading == null) {
            isdownloading = 0;
        }
        return isdownloading;
    }

    /**
     * @param isdownloading the isdownloading to set
     */
    public void setIsdownloading(Integer isdownloading) {
        this.isdownloading = isdownloading;
    }

    /**
     * @return the siteTags
     */
    public String getSiteTags() {
        return siteTags;
    }

    /**
     * @param siteTags the siteTags to set
     */
    public void setSiteTags(String siteTags) {
        this.siteTags = siteTags;
    }

    public Integer getIdToDownload() {
        return idToDownload;
    }

    public void setIdToDownload(Integer idToDownload) {
        this.idToDownload = idToDownload;
    }

    public Long getDateAddedToQueue() {
        return dateAddedToQueue;
    }

    public void setDateAddedToQueue(Long dateAddedToQueue) {
        this.dateAddedToQueue = dateAddedToQueue;
    }

    public Long getDateFinishedDownload() {
        return dateFinishedDownload;
    }

    public void setDateFinishedDownload(Long dateFinishedDownload) {
        this.dateFinishedDownload = dateFinishedDownload;
    }

    public Long getLastVerified() {
        return lastVerified;
    }

    public void setLastVerified(Long lastVerified) {
        this.lastVerified = lastVerified;
    }
}