package com.elite.rideplatform.pricing;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SurgePricingRepository extends JpaRepository<SurgePricing, UUID> {
    
    @Query("SELECT s FROM SurgePricing s WHERE s.isActive = true " +
           "AND s.latMin <= :lat AND s.latMax >= :lat " +
           "AND s.lngMin <= :lng AND s.lngMax >= :lng " +
           "ORDER BY s.multiplier DESC LIMIT 1")
    Optional<SurgePricing> findActiveSurgePricing(@Param("lat") double lat, @Param("lng") double lng);
}
