package com.trip.repositories;

import com.trip.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // add custom queries if needed
}