package com.elite.rideplatform.pricing;

import com.elite.rideplatform.exception.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PricingService {

    private final FareRuleRepository fareRuleRepository;
    private final SurgePricingRepository surgePricingRepository;

    public PricingService(FareRuleRepository fareRuleRepository, SurgePricingRepository surgePricingRepository) {
        this.fareRuleRepository = fareRuleRepository;
        this.surgePricingRepository = surgePricingRepository;
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateFare(String vehicleCategory, double pickupLat, double pickupLng, double dropoffLat, double dropoffLng) {
        FareRule rule = fareRuleRepository.findByVehicleCategory(vehicleCategory)
                .orElseThrow(() -> new DomainException("Fare rules not found for category: " + vehicleCategory));

        double distanceKm = calculateDistance(pickupLat, pickupLng, dropoffLat, dropoffLng);
        int estimatedDurationMin = (int) (distanceKm / 0.5);
        if (distanceKm == 0) estimatedDurationMin = 1;

        SurgePricing surge = surgePricingRepository.findActiveSurgePricing(pickupLat, pickupLng).orElse(null);
        BigDecimal surgeMultiplier = surge != null ? surge.getMultiplier() : BigDecimal.ONE;

        BigDecimal distanceCharge = BigDecimal.valueOf(distanceKm).multiply(rule.getPerKmRate());
        BigDecimal timeCharge = BigDecimal.valueOf(estimatedDurationMin).multiply(rule.getPerMinRate());
        
        BigDecimal rawSubtotal = rule.getBaseFare().add(distanceCharge).add(timeCharge);
        BigDecimal priceAfterMinCheck = rawSubtotal.max(rule.getMinFare());
        
        return priceAfterMinCheck.multiply(surgeMultiplier);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth Radius in KM
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; 
    }
}
