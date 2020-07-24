package org.lemanoman.videoviz.dto;

import java.util.List;

/**
 *
 * @author Kevim Such
 */
public class ScrapResult {
    private String title;
    private String midiaUrl;
    private List<String> tags;

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
     * @return the tags
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    
}