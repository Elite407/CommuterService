package com.elite.rideplatform.exception;

public class TripNotFoundException extends DomainException {
    public TripNotFoundException(String message) {
        super(message);
    }
}
