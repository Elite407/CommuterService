package com.elite.rideplatform.pricing;

import com.elite.rideplatform.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "rp_offers")
public class Offer extends BaseEntity {

    @Column(name = "promo_code", nullable = false, unique = true)
    private String promoCode;

    @Column(name = "discount_pct")
    private BigDecimal discountPct;

    @Column(name = "flat_discount")
    private BigDecimal flatDiscount;

    @Column(name = "max_discount", nullable = false)
    private BigDecimal maxDiscount;

    @Column(name = "min_trip_value", nullable = false)
    private BigDecimal minTripValue;

    @Column(name = "valid_from", nullable = false)
    private OffsetDateTime validFrom;

    @Column(name = "valid_until", nullable = false)
    private OffsetDateTime validUntil;

    @Column(name = "usage_limit", nullable = false)
    private int usageLimit;

    @Column(name = "total_used", nullable = false)
    private int totalUsed = 0;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // Getters and Setters
    public String getPromoCode() { return promoCode; }
    public void setPromoCode(String promoCode) { this.promoCode = promoCode; }

    public BigDecimal getDiscountPct() { return discountPct; }
    public void setDiscountPct(BigDecimal discountPct) { this.discountPct = discountPct; }

    public BigDecimal getFlatDiscount() { return flatDiscount; }
    public void setFlatDiscount(BigDecimal flatDiscount) { this.flatDiscount = flatDiscount; }

    public BigDecimal getMaxDiscount() { return maxDiscount; }
    public void setMaxDiscount(BigDecimal maxDiscount) { this.maxDiscount = maxDiscount; }

    public BigDecimal getMinTripValue() { return minTripValue; }
    public void setMinTripValue(BigDecimal minTripValue) { this.minTripValue = minTripValue; }

    public OffsetDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(OffsetDateTime validFrom) { this.validFrom = validFrom; }

    public OffsetDateTime getValidUntil() { return validUntil; }
    public void setValidUntil(OffsetDateTime validUntil) { this.validUntil = validUntil; }

    public int getUsageLimit() { return usageLimit; }
    public void setUsageLimit(int usageLimit) { this.usageLimit = usageLimit; }

    public int getTotalUsed() { return totalUsed; }
    public void setTotalUsed(int totalUsed) { this.totalUsed = totalUsed; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
