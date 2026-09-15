package com.elite.rideplatform.pricing;

import com.elite.rideplatform.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "rp_fare_rules")
public class FareRule extends BaseEntity {
    
    @Column(name = "vehicle_category", nullable = false)
    private String vehicleCategory;
    
    @Column(name = "base_fare", nullable = false)
    private BigDecimal baseFare;
    
    @Column(name = "per_km_rate", nullable = false)
    private BigDecimal perKmRate;
    
    @Column(name = "per_min_rate", nullable = false)
    private BigDecimal perMinRate;
    
    @Column(name = "min_fare", nullable = false)
    private BigDecimal minFare;

    public FareRule() {}

    public String getVehicleCategory() { return vehicleCategory; }
    public void setVehicleCategory(String vehicleCategory) { this.vehicleCategory = vehicleCategory; }

    public BigDecimal getBaseFare() { return baseFare; }
    public void setBaseFare(BigDecimal baseFare) { this.baseFare = baseFare; }

    public BigDecimal getPerKmRate() { return perKmRate; }
    public void setPerKmRate(BigDecimal perKmRate) { this.perKmRate = perKmRate; }

    public BigDecimal getPerMinRate() { return perMinRate; }
    public void setPerMinRate(BigDecimal perMinRate) { this.perMinRate = perMinRate; }

    public BigDecimal getMinFare() { return minFare; }
    public void setMinFare(BigDecimal minFare) { this.minFare = minFare; }
}
