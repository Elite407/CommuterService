package com.elite.rideplatform.partner;

import com.elite.rideplatform.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "rp_partner_sessions")
public class PartnerSession extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false, unique = true)
    private Partner partner;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "last_known_lat")
    private Double lastKnownLat;

    @Column(name = "last_known_lng")
    private Double lastKnownLng;

    // Getters and Setters
    public Partner getPartner() { return partner; }
    public void setPartner(Partner partner) { this.partner = partner; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getLastKnownLat() { return lastKnownLat; }
    public void setLastKnownLat(Double lastKnownLat) { this.lastKnownLat = lastKnownLat; }

    public Double getLastKnownLng() { return lastKnownLng; }
    public void setLastKnownLng(Double lastKnownLng) { this.lastKnownLng = lastKnownLng; }
}
