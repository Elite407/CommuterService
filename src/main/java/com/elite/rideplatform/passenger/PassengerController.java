package com.elite.rideplatform.passenger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/passengers")
public class PassengerController {

    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @PostMapping("/register")
    public ResponseEntity<Passenger> register(@RequestBody PassengerRegistrationRequest request) {
        Passenger newPassenger = passengerService.registerNewPassenger(
                request.getFirstName(), request.getMiddleName(), request.getLastName(),
                request.getEmail(), request.getPhoneNo(), request.getPassword()
        );
        // Exclude sensitive data from response in real-world, but returning object for simplicity here.
        return ResponseEntity.ok(newPassenger);
    }
}
