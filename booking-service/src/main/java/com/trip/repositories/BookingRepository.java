package com.trip.repositories;

import com.trip.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("SELECT b FROM Booking b WHERE b.status = 'PENDING' AND b.bookingDate < :cutoff")
    List<Booking> findAllPendingOlderThan(Instant cutoff);
    // add custom queries if needed

    long countByFlightId(Long flightId);

}