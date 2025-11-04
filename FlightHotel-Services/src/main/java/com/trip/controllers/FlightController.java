package com.trip.controllers;

import com.trip.entities.Flight;
import com.trip.repositories.FlightRepository;
import com.trip.services.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/flights")
@RequiredArgsConstructor
public class FlightController {
    private final FlightService flightService;
    private final FlightRepository flightRepository;

    @GetMapping
    public List<Flight> getAllFlights() {
        return flightService.getAllFlights();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin')")
    public Flight addFlight(@RequestBody Flight flight) {
        return flightService.addFlight(flight);
    }
    @GetMapping("/{id}")
    public Flight getFlightById(@PathVariable Long id) {
        return flightService.getFlightById(id);
    }
    @DeleteMapping("/cleanup-old")
    public ResponseEntity<String> cleanupOldFlights() {
        flightService.deleteOldFlights();
        return ResponseEntity.ok("Old flights cleaned up successfully.");
    }
    @PutMapping("/{flightId}/available")
    public ResponseEntity<String> makeFlightAvailable(@PathVariable Long flightId) {
        Optional<Flight> flightOpt = flightRepository.findById(flightId);
        if (flightOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Flight flight = flightOpt.get();
        flight.setAvailable(true); // أو flight.setStatus("AVAILABLE");
        flightRepository.save(flight);

        return ResponseEntity.ok("Flight " + flightId + " is now available again.");
    }
    @PutMapping("/{flightId}/unavailable")
    public ResponseEntity<String> makeFlightUnavailable(@PathVariable Long flightId) {
        Optional<Flight> flightOpt = flightRepository.findById(flightId);
        if (flightOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Flight flight = flightOpt.get();
        flight.setAvailable(false);
        flightRepository.save(flight);
        return ResponseEntity.ok("Flight " + flightId + " is now unavailable.");
    }



}

