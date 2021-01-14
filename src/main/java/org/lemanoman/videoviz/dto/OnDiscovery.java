package org.lemanoman.videoviz.dto;

import org.lemanoman.videoviz.model.LocationModel;

public interface OnDiscovery {
    void onFinish(LocationModel locationModel, Integer totalFiles);
}
