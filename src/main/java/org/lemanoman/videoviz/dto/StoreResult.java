package org.lemanoman.videoviz.dto;

import java.io.File;

public class StoreResult {
    private File videoAdded;
    private String md5sum;

    public File getVideoAdded() {
        return videoAdded;
    }

    public void setVideoAdded(File videoAdded) {
        this.videoAdded = videoAdded;
    }

    public String getMd5sum() {
        return md5sum;
    }

    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }
}
