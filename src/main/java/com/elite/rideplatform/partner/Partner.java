package com.elite.rideplatform.partner;

import com.elite.rideplatform.common.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "rp_partners")
public class Partner extends User {
    
    @Column(name = "license_no", nullable = false, unique = true)
    private String licenseNo;
    
    @Column(name = "avg_rating")
    private double avgRating;
    
    @Column(name = "total_trips")
    private int totalTrips;
    
    @Column(name = "is_verified")
    private boolean isVerified;

    public Partner() {
        super();
    }

    public Partner(String firstName, String middleName, String lastName, 
                   String email, String phoneNo, String passwordHash, String licenseNo) {
        super(firstName, middleName, lastName, email, phoneNo, passwordHash);
        this.licenseNo = licenseNo;
        this.avgRating = 0.0;
        this.totalTrips = 0;
        this.isVerified = false;
    }

    public String getLicenseNo() { return licenseNo; }
    public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }

    public double getAvgRating() { return avgRating; }
    public void setAvgRating(double avgRating) { this.avgRating = avgRating; }

    public int getTotalTrips() { return totalTrips; }
    public void setTotalTrips(int totalTrips) { this.totalTrips = totalTrips; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean isVerified) { this.isVerified = isVerified; }
}
