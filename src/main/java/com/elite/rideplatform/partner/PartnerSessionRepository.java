package com.elite.rideplatform.partner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PartnerSessionRepository extends JpaRepository<PartnerSession, UUID> {
}
