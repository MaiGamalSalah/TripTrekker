
package com.trip.controllers;


import com.trip.dto.FlightDTO;
import com.trip.dto.HotelDTO;
import com.trip.services.SearchService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.annotation.Cacheable;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
        import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final RestTemplate restTemplate;
    private final SearchService searchService;


    private static final String FLIGHT_SERVICE_URL = "http://localhost:8083/flights";
    private static final String HOTEL_SERVICE_URL = "http://localhost:8083/hotels";

    @GetMapping("/flights")
    public List<?> searchFlights(@RequestParam String from,
                                 @RequestParam String to,
                                 @RequestParam String date) {
        String key = String.format("flights:%s:%s:%s", from, to, date);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            log.info("\u001B[32m [CACHE HIT] Flights fetched from Redis for key: {}\u001B[0m", key);
            return (List<?>) redisTemplate.opsForValue().get(key);
        }

        log.info("\u001B[33m [CACHE MISS] Fetching flights from Flight Service for key: {}\u001B[0m", key);
        List<?> flights = restTemplate.getForObject(
                FLIGHT_SERVICE_URL + "?from={from}&to={to}&date={date}",
                List.class,
                Map.of("from", from, "to", to, "date", date)
        );

        redisTemplate.opsForValue().set(key, flights);
        log.info("\u001B[34m Flights cached in Redis for key: {}\u001B[0m", key);

        return flights;
    }

     //search/hotels?city=Cairo&date=2025-11-10


    @GetMapping("/hotels")
    public List<?> searchHotels(@RequestParam String city,
                                @RequestParam String date) {
        String key = String.format("hotels:%s:%s", city, date);

        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            log.info("\u001B[32m [CACHE HIT] Hotels fetched from Redis for key: {}\u001B[0m", key);
            return (List<?>) redisTemplate.opsForValue().get(key);
        }

        log.info("\u001B[33m [CACHE MISS] Fetching hotels from Hotel Service for key: {}\u001B[0m", key);
        List<?> hotels = restTemplate.getForObject(
                HOTEL_SERVICE_URL + "?city={city}&date={date}",
                List.class,
                Map.of("city", city, "date", date)
        );

        redisTemplate.opsForValue().set(key, hotels);
        log.info("\u001B[34m Hotels cached in Redis for key: {}\u001B[0m", key);

        return hotels;
    }
    @GetMapping("/all-flights")
    @Cacheable(value = "search", key = "'all-flights'")
    public List<FlightDTO> getAllFlights() {
        log.info("[CACHE MISS] Fetching ALL flights from FlightHotelService");

        return searchService.getAllFlights();
    }
    @GetMapping("/all-hotels")
    @Cacheable(value = "search", key = "'all-hotels'")
    public List<HotelDTO> getAllHotels() {
        log.info("[CACHE MISS] Fetching ALL hotels from FlightHotelService");
        return searchService.getAllHotels();
    }
//    @DeleteMapping("/cache/clear")
//    @CacheEvict(value = "search", allEntries = true)
//    public String clearCache() {
//        return " Search cache cleared successfully.";
//    }

}
