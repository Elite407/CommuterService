package com.elite.rideplatform.trip;

import com.elite.rideplatform.common.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "rp_ratings")
public class Rating extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", nullable = false, unique = true)
    private Trip trip;

    @Column(name = "rating_for_partner")
    private Integer ratingForPartner;

    @Column(name = "partner_comment")
    private String partnerComment;

    @Column(name = "rating_for_passenger")
    private Integer ratingForPassenger;

    @Column(name = "passenger_comment")
    private String passengerComment;

    // Getters and Setters
    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }

    public Integer getRatingForPartner() { return ratingForPartner; }
    public void setRatingForPartner(Integer ratingForPartner) { this.ratingForPartner = ratingForPartner; }

    public String getPartnerComment() { return partnerComment; }
    public void setPartnerComment(String partnerComment) { this.partnerComment = partnerComment; }

    public Integer getRatingForPassenger() { return ratingForPassenger; }
    public void setRatingForPassenger(Integer ratingForPassenger) { this.ratingForPassenger = ratingForPassenger; }

    public String getPassengerComment() { return passengerComment; }
    public void setPassengerComment(String passengerComment) { this.passengerComment = passengerComment; }
}
