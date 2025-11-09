package com.trip.services;

import com.trip.entities.Flight;
import com.trip.repositories.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {
    private final FlightRepository flightRepository;

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Flight addFlight(Flight flight) {
        return flightRepository.save(flight);
    }



    public Flight getFlightById(Long id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found with id " + id));
    }


    public void makeFlightAvailable(Long id, boolean available) {
        Flight f = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight not found"));
        f.setAvailable(available);
        flightRepository.save(f);
    }

    //@Scheduled(cron = "0 0 3 * * *")
    @Scheduled(fixedRate = 60000)
    public void deleteOldFlights() {
        List<Flight> oldFlights = flightRepository
                .findByDepartureTimeBeforeOrDepartureTimeIsNull(LocalDateTime.now());
        if (oldFlights.isEmpty()) return;
        flightRepository.deleteAll(oldFlights);
        System.out.println("Deleted " + oldFlights.size() + " old or invalid flights at " + LocalDateTime.now());
    }


}