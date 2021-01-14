package org.lemanoman.videoviz.repositories;

import org.lemanoman.videoviz.model.CheckupModel;
import org.lemanoman.videoviz.model.DownloadQueue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface CheckupRepository extends JpaRepository <CheckupModel, Integer> {
    List<CheckupModel> findByRunningAndFinishedAndLastVerifiedDate(Integer running,Integer finished, Date lastVerifiedDate);
    List<CheckupModel> findByRunningAndFinished(Integer running,Integer finished);

}
