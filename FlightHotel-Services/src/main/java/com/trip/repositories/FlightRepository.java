package com.trip.repositories;

import com.trip.entities.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    ///List<Flight> findByDepartureTimeBefore(LocalDateTime time);
    List<Flight> findByDepartureTimeBeforeOrDepartureTimeIsNull(LocalDateTime time);

}