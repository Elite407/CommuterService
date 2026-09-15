package com.elite.rideplatform.passenger;

import com.elite.rideplatform.common.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "rp_passengers")
public class Passenger extends User {
    
    @Column(name = "is_verified")
    private boolean isVerified;

    public Passenger() {
        super();
    }

    public Passenger(String firstName, String middleName, String lastName, 
                     String email, String phoneNo, String passwordHash) {
        super(firstName, middleName, lastName, email, phoneNo, passwordHash);
        this.isVerified = false;
    }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean isVerified) { this.isVerified = isVerified; }
}
