package com.elite.rideplatform.partner;

import com.elite.rideplatform.exception.DomainException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final PasswordEncoder passwordEncoder;

    public PartnerService(PartnerRepository partnerRepository, PasswordEncoder passwordEncoder) {
        this.partnerRepository = partnerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Partner registerNewPartner(String firstName, String middleName, String lastName, 
                                      String email, String phoneNo, String password, String licenseNo) {
        
        if (password == null || password.length() < 6) {
            throw new DomainException("Password must be at least 6 characters.");
        }

        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new DomainException("Invalid email format.");
        }

        if (licenseNo == null || licenseNo.trim().isEmpty()) {
            throw new DomainException("A valid License Number is required.");
        }

        if (partnerRepository.existsByEmailOrPhoneNoOrLicenseNo(email, phoneNo, licenseNo)) {
            throw new DomainException("Email, Phone, or License already exists.");
        }

        String hashedPassword = passwordEncoder.encode(password);
        Partner newPartner = new Partner(firstName, middleName, lastName, email, phoneNo, hashedPassword, licenseNo);

        return partnerRepository.save(newPartner);
    }
}
