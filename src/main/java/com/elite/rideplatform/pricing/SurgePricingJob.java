package com.elite.rideplatform.pricing;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

@Component
public class SurgePricingJob {

    private final SurgePricingRepository surgePricingRepository;
    private final Random random = new Random();

    public SurgePricingJob(SurgePricingRepository surgePricingRepository) {
        this.surgePricingRepository = surgePricingRepository;
    }

    /**
     * Simulates dynamic pricing updates every 5 minutes.
     * In a real application, this would calculate demand/supply ratios.
     */
    @Scheduled(fixedRate = 300000) // 5 minutes in milliseconds
    @Transactional
    public void updateSurgeMultipliers() {
        List<SurgePricing> activeSurges = surgePricingRepository.findAll(); // simplified for demo
        
        for (SurgePricing surge : activeSurges) {
            // Generate a random multiplier between 1.0 and 2.5
            double newMultiplier = 1.0 + (1.5 * random.nextDouble());
            BigDecimal roundedMultiplier = BigDecimal.valueOf(newMultiplier).setScale(2, RoundingMode.HALF_UP);
            
            surge.setMultiplier(roundedMultiplier);
            surgePricingRepository.save(surge);
        }
    }
}
