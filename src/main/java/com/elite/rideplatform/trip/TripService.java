package com.elite.rideplatform.trip;

import com.elite.rideplatform.exception.DomainException;
import com.elite.rideplatform.passenger.Passenger;
import com.elite.rideplatform.passenger.PassengerRepository;
import com.elite.rideplatform.partner.Partner;
import com.elite.rideplatform.partner.PartnerRepository;
import com.elite.rideplatform.pricing.PricingService;
import com.elite.rideplatform.wallet.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final PassengerRepository passengerRepository;
    private final PartnerRepository partnerRepository;
    private final PricingService pricingService;
    private final WalletService walletService;

    public TripService(TripRepository tripRepository, 
                       PassengerRepository passengerRepository, 
                       PartnerRepository partnerRepository, 
                       PricingService pricingService, 
                       WalletService walletService) {
        this.tripRepository = tripRepository;
        this.passengerRepository = passengerRepository;
        this.partnerRepository = partnerRepository;
        this.pricingService = pricingService;
        this.walletService = walletService;
    }

    @Transactional
    public Trip requestTrip(UUID passengerId, String vehicleCategory, 
                            String pickupAddress, double pickupLat, double pickupLng, 
                            String dropoffAddress, double dropoffLat, double dropoffLng) {
        
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new DomainException("Passenger not found"));

        BigDecimal estimatedFare = pricingService.calculateFare(vehicleCategory, pickupLat, pickupLng, dropoffLat, dropoffLng);
        
        Trip trip = new Trip();
        trip.setPassenger(passenger);
        trip.setPickupAddress(pickupAddress);
        trip.setPickupLat(pickupLat);
        trip.setPickupLng(pickupLng);
        trip.setDropoffAddress(dropoffAddress);
        trip.setDropoffLat(dropoffLat);
        trip.setDropoffLng(dropoffLng);
        trip.setStatus(TripStatus.REQUESTED);
        trip.setFinalFare(estimatedFare);
        trip.setRequestedAt(OffsetDateTime.now());
        
        return tripRepository.save(trip);
    }

    @Transactional
    public void acceptTrip(UUID tripId, UUID partnerId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new DomainException("Trip not found"));
        
        if (trip.getStatus() != TripStatus.REQUESTED) {
            throw new DomainException("Trip is no longer available.");
        }

        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new DomainException("Partner not found"));

        trip.setPartner(partner);
        trip.setStatus(TripStatus.ACCEPTED);
        tripRepository.save(trip);
    }

    @Transactional
    public void arriveAtPickup(UUID tripId, UUID partnerId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new DomainException("Trip not found"));
        
        if (trip.getStatus() != TripStatus.ACCEPTED) {
            throw new DomainException("Invalid trip state for arrival.");
        }
        
        // Ensure the partner arriving is the one who accepted
        if (!trip.getPartner().getId().equals(partnerId)) {
            throw new DomainException("Unauthorized partner.");
        }

        trip.setStatus(TripStatus.ARRIVED);
        tripRepository.save(trip);
    }

    @Transactional
    public void startTrip(UUID tripId, UUID partnerId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new DomainException("Trip not found"));
        
        if (trip.getStatus() != TripStatus.ARRIVED) {
            throw new DomainException("Invalid trip state for starting.");
        }

        if (!trip.getPartner().getId().equals(partnerId)) {
            throw new DomainException("Unauthorized partner.");
        }

        trip.setStatus(TripStatus.IN_PROGRESS);
        tripRepository.save(trip);
    }

    @Transactional
    public void completeTrip(UUID tripId, UUID partnerId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new DomainException("Trip not found"));
        
        if (trip.getStatus() != TripStatus.IN_PROGRESS) {
            throw new DomainException("Invalid trip state for completion. Trip must be IN_PROGRESS.");
        }

        if (!trip.getPartner().getId().equals(partnerId)) {
            throw new DomainException("Unauthorized partner.");
        }
        
        trip.setStatus(TripStatus.COMPLETED);
        tripRepository.save(trip);
        
        // Example: Credit 80% to partner wallet
        if (trip.getPartner() != null && trip.getFinalFare() != null) {
            BigDecimal partnerCut = trip.getFinalFare().multiply(new BigDecimal("0.8"));
            walletService.creditWallet(trip.getPartner().getId(), partnerCut);
        }
    }
}
