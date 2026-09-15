package com.elite.rideplatform.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class User extends BaseEntity {
    
    @Column(name = "first_name", nullable = false)
    protected String firstName;
    
    @Column(name = "middle_name")
    protected String middleName;
    
    @Column(name = "last_name", nullable = false)
    protected String lastName;
    
    @Column(name = "email", nullable = false, unique = true)
    protected String email;
    
    @Column(name = "phone_no", nullable = false, unique = true)
    protected String phoneNo;
    
    @Column(name = "password_hash", nullable = false)
    protected String passwordHash;

    public User() {}

    public User(String firstName, String middleName, String lastName, 
                String email, String phoneNo, String passwordHash) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNo = phoneNo;
        this.passwordHash = passwordHash;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNo() { return phoneNo; }
    public void setPhoneNo(String phoneNo) { this.phoneNo = phoneNo; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
