package com.elite.rideplatform.passenger;

import com.elite.rideplatform.exception.DomainException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PassengerService {

    private final PassengerRepository passengerRepository;
    private final PasswordEncoder passwordEncoder;

    public PassengerService(PassengerRepository passengerRepository, PasswordEncoder passwordEncoder) {
        this.passengerRepository = passengerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Passenger registerNewPassenger(String firstName, String middleName, String lastName, 
                                          String email, String phoneNo, String password) {
        
        if (password == null || password.length() < 6) {
            throw new DomainException("Password must be at least 6 characters.");
        }

        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new DomainException("Invalid email format.");
        }

        if (passengerRepository.existsByEmailOrPhoneNo(email, phoneNo)) {
            throw new DomainException("Email or Phone Number already exists.");
        }

        String hashedPassword = passwordEncoder.encode(password);
        Passenger newPassenger = new Passenger(firstName, middleName, lastName, email, phoneNo, hashedPassword);

        return passengerRepository.save(newPassenger);
    }
}
