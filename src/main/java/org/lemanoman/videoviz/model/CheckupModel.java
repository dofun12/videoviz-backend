package org.lemanoman.videoviz.model;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "checkup")
public class CheckupModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, nullable = false)
    private Integer id;

    @Column(name = "totalVerified")
    private Integer totalVerified;

    @Column(name = "statusMessage")
    private String statusMessage;

    @Column(name = "finished")
    private Integer finished;

    @Column(name = "running")
    private Integer running;

    @Column(name = "lastVerifiedDate")
    private Timestamp lastVerifiedDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTotalVerified() {
        return totalVerified;
    }

    public void setTotalVerified(Integer totalVerified) {
        this.totalVerified = totalVerified;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Integer getFinished() {
        return finished;
    }

    public void setFinished(Integer finished) {
        this.finished = finished;
    }

    public Integer getRunning() {
        return running;
    }

    public void setRunning(Integer running) {
        this.running = running;
    }

    public Timestamp getLastVerifiedDate() {
        return lastVerifiedDate;
    }

    public void setLastVerifiedDate(Timestamp lastVerifiedDate) {
        this.lastVerifiedDate = lastVerifiedDate;
    }
}
