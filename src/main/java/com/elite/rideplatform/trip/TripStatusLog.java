package com.elite.rideplatform.trip;

import com.elite.rideplatform.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "rp_trip_status_log")
public class TripStatusLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "changed_by", nullable = false)
    private String changedBy;

    // Getters and Setters
    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
}
