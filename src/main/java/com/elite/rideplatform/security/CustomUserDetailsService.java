package com.elite.rideplatform.security;

import com.elite.rideplatform.passenger.Passenger;
import com.elite.rideplatform.passenger.PassengerRepository;
import com.elite.rideplatform.partner.Partner;
import com.elite.rideplatform.partner.PartnerRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PassengerRepository passengerRepository;
    private final PartnerRepository partnerRepository;

    public CustomUserDetailsService(PassengerRepository passengerRepository, PartnerRepository partnerRepository) {
        this.passengerRepository = passengerRepository;
        this.partnerRepository = partnerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // First check if it's a Passenger
        Optional<Passenger> passengerOpt = passengerRepository.findByEmail(email);
        if (passengerOpt.isPresent()) {
            Passenger passenger = passengerOpt.get();
            return new org.springframework.security.core.userdetails.User(
                    passenger.getEmail(),
                    passenger.getPasswordHash(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_PASSENGER"))
            );
        }

        // Next check if it's a Partner
        Optional<Partner> partnerOpt = partnerRepository.findByEmail(email);
        if (partnerOpt.isPresent()) {
            Partner partner = partnerOpt.get();
            return new org.springframework.security.core.userdetails.User(
                    partner.getEmail(),
                    partner.getPasswordHash(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_PARTNER"))
            );
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
