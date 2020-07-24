package org.lemanoman.videoviz.repositories;

import org.lemanoman.videoviz.model.DownloadQueue;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DownloadQueueRepository extends JpaRepository <DownloadQueue, Integer> {
    @Override
    Optional<DownloadQueue> findById(Integer id);

    List<DownloadQueue> findTop10ByFinishedAndInProgress(Integer finished, Integer inProgress);

    List<DownloadQueue> findByFinishedAndInProgress(Integer finished, Integer inProgress);


}
