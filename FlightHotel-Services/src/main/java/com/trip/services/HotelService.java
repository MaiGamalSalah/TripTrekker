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
//    @Scheduled(cron = "0 10 3 * * *")
//    public void cleanupUnavailableHotels() {
//        List<Hotel> unavailableHotels = hotelRepository.findByAvailableFalse();
//        if (unavailableHotels.isEmpty()) return;
//        System.out.println("Found " + unavailableHotels.size() + " unavailable hotels — checking...");
//    }

}
