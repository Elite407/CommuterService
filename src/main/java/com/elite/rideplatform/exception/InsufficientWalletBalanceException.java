package com.elite.rideplatform.exception;

public class InsufficientWalletBalanceException extends DomainException {
    public InsufficientWalletBalanceException(String message) {
        super(message);
    }
}
