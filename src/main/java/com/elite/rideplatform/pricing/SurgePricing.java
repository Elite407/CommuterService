package com.elite.rideplatform.pricing;

import com.elite.rideplatform.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
@Table(name = "rp_surge_pricing")
public class SurgePricing extends BaseEntity {
    
    @Column(name = "zone_name", nullable = false)
    private String zoneName;
    
    @Column(name = "multiplier", nullable = false)
    private BigDecimal multiplier;

    @Column(name = "lat_min", nullable = false)
    private double latMin;
    @Column(name = "lat_max", nullable = false)
    private double latMax;
    @Column(name = "lng_min", nullable = false)
    private double lngMin;
    @Column(name = "lng_max", nullable = false)
    private double lngMax;
    
    @Column(name = "day_of_week", nullable = false)
    private String dayOfWeek;
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    
    @Column(name = "is_active")
    private boolean isActive = true;

    public SurgePricing() {}

    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }
    
    public BigDecimal getMultiplier() { return multiplier; }
    public void setMultiplier(BigDecimal multiplier) { this.multiplier = multiplier; }

    public double getLatMin() { return latMin; }
    public void setLatMin(double latMin) { this.latMin = latMin; }
    
    public double getLatMax() { return latMax; }
    public void setLatMax(double latMax) { this.latMax = latMax; }
    
    public double getLngMin() { return lngMin; }
    public void setLngMin(double lngMin) { this.lngMin = lngMin; }
    
    public double getLngMax() { return lngMax; }
    public void setLngMax(double lngMax) { this.lngMax = lngMax; }
    
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
