package com.elite.rideplatform.partner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    List<Vehicle> findByPartnerId(UUID partnerId);
    boolean existsByPlateNo(String plateNo);
}
