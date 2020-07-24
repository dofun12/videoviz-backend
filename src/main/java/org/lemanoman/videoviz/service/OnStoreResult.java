package org.lemanoman.videoviz.service;

import org.lemanoman.videoviz.model.DownloadQueue;

import java.io.File;

public interface OnStoreResult {
    void onServiceStart(DownloadQueue queue);
    void onDownloadStart(DownloadQueue queue, long size);
    void onDownloadError(Exception ex);
    void onDownloadSuccess(DownloadQueue queue);
    void onPermissionSuccess(DownloadQueue queue);
    void onReadyToFactoryImage(File mp4File, DownloadQueue queue);
    void onFinished(DownloadQueue downloadQueue, File file1);
}
