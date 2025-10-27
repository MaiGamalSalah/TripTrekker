package com.trip.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
@Data
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @Column(name = "user_id", nullable = false)
    private String userId; // UUID  Keycloak

    @Column(name = "flight_id")
    private Long flightId; // optional

    @Column(name = "hotel_id")
    private Long hotelId; // optional

    @Column(name = "booking_date", nullable = false)
    private Instant bookingDate;

    @Column(name = "status", nullable = false)
    private String status; // PENDING / PAID / CANCELLED
}
