package com.trip.services;

import com.trip.entities.Hotel;
import com.trip.repositories.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {
    private final HotelRepository hotelRepository;

    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    public Hotel addHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    public Hotel getHotelById(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id " + id));
    }

    public void makeHotelAvailable(Long id, boolean available) {
        Hotel h = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
        h.setAvailable(available);
        hotelRepository.save(h);
    }


}
