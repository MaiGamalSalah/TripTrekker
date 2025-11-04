package com.trip.controllers;

import com.trip.entities.Hotel;
import com.trip.repositories.HotelRepository;
import com.trip.services.FlightService;
import com.trip.services.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelController {
    private final HotelService hotelService;
    private final HotelRepository hotelRepository;

    @GetMapping
    public List<Hotel> getAllHotels() {
        return hotelService.getAllHotels();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin')")
    public Hotel addHotel(@RequestBody Hotel hotel) {
        return hotelService.addHotel(hotel);
    }
    @GetMapping("/{id}")
    public Hotel getHotelById(@PathVariable Long id) {
        return hotelService.getHotelById(id);
    }


    //  لما الفندق يتحجز
    @PutMapping("/{hotelId}/unavailable")
    public ResponseEntity<String> makeHotelUnavailable(@PathVariable Long hotelId) {
        Optional<Hotel> hotelOpt = hotelRepository.findById(hotelId);
        if (hotelOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Hotel hotel = hotelOpt.get();
        hotel.setAvailable(false);
        hotelRepository.save(hotel);

        return ResponseEntity.ok("Hotel " + hotelId + " marked as unavailable.");
    }

    //  لما الفندق يبقى متاح تاني
    @PutMapping("/{hotelId}/available")
    public ResponseEntity<String> makeHotelAvailable(@PathVariable Long hotelId) {
        Optional<Hotel> hotelOpt = hotelRepository.findById(hotelId);
        if (hotelOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Hotel hotel = hotelOpt.get();
        hotel.setAvailable(true);
        hotelRepository.save(hotel);

        return ResponseEntity.ok("Hotel " + hotelId + " is now available again.");
    }


}
