package com.elite.rideplatform.trip;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @PostMapping("/request")
    public ResponseEntity<Trip> requestTrip(@RequestBody TripRequest request) {
        Trip trip = tripService.requestTrip(
                request.getPassengerId(), request.getVehicleCategory(),
                request.getPickupAddress(), request.getPickupLat(), request.getPickupLng(),
                request.getDropoffAddress(), request.getDropoffLat(), request.getDropoffLng()
        );
        return ResponseEntity.ok(trip);
    }

    @PostMapping("/{tripId}/accept")
    public ResponseEntity<Void> acceptTrip(@PathVariable UUID tripId, @RequestParam UUID partnerId) {
        tripService.acceptTrip(tripId, partnerId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{tripId}/arrive")
    public ResponseEntity<Void> arriveAtPickup(@PathVariable UUID tripId, @RequestParam UUID partnerId) {
        tripService.arriveAtPickup(tripId, partnerId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{tripId}/start")
    public ResponseEntity<Void> startTrip(@PathVariable UUID tripId, @RequestParam UUID partnerId) {
        tripService.startTrip(tripId, partnerId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{tripId}/complete")
    public ResponseEntity<Void> completeTrip(@PathVariable UUID tripId, @RequestParam UUID partnerId) {
        tripService.completeTrip(tripId, partnerId);
        return ResponseEntity.ok().build();
    }
}
