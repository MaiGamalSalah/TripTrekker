package com.trip.services;

import com.trip.dto.FlightDTO;
import com.trip.dto.HotelDTO;
import org.springframework.cache.annotation.Cacheable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;


import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final RestTemplate restTemplate;
    private final CacheManager cacheManager;

    @Value("${flight-hotel-service.url}")
    private String flightHotelServiceUrl;
//@Cacheable(cacheNames = "search",
//            key = "T(org.springframework.util.StringUtils).trimAllWhitespace(#from.toLowerCase()) + ':' + T(org.springframework.util.StringUtils).trimAllWhitespace(#to.toLowerCase()) + ':' + (#date == null ? 'any' : #date)")
    public List<?> searchFlights(String from, String to, String date) {
        String key = from.trim().toLowerCase() + ":" + to.trim().toLowerCase() + ":" + (date == null ? "any" : date);
        Cache cache = cacheManager.getCache("search");

        // Check if data exists in cache first
        if (cache != null && cache.get(key) != null) {
            log.info("Cache HIT → Returning flights from Redis for {}", key);
            return (List<?>) cache.get(key).get();
        }

        // Otherwise → call FlightHotel service
        log.info("Cache MISS → Fetching flights from FlightHotel Service for {}", key);

        String url = String.format("%s/flights?origin=%s&destination=%s%s",
                flightHotelServiceUrl,
                from.trim(),
                to.trim(),
                (date != null ? "&date=" + date : ""));

        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
        List<?> result = Objects.requireNonNullElse(response.getBody(), List.of());

        // Save to cache manually
        if (cache != null) cache.put(key, result);

        return result;
    }


    // @Cacheable(cacheNames = "search-hotels",
    //            key = "T(org.springframework.util.StringUtils).trimAllWhitespace(#city.toLowerCase()) + ':' + (#date == null ? 'any' : #date)")

    public List<?> searchHotels(String city, String date) {
        String key = city.trim().toLowerCase() + ":" + (date == null ? "any" : date);
        Cache cache = cacheManager.getCache("search-hotels");

        if (cache != null && cache.get(key) != null) {
            log.info("Cache HIT → Returning hotels from Redis for {}", key);
            return (List<?>) cache.get(key).get();
        }

        log.info("Cache MISS → Fetching hotels from FlightHotel Service for {}", key);

        String url = String.format("%s/hotels?city=%s%s",
                flightHotelServiceUrl,
                city.trim(),
                (date != null ? "&date=" + date : ""));

        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
        List<?> result = Objects.requireNonNullElse(response.getBody(), List.of());

        if (cache != null) cache.put(key, result);

        return result;
    }

    @Cacheable(value = "search", key = "'all-flights'")

    public List<FlightDTO> getAllFlights() {
        String url = flightHotelServiceUrl + "/flights";
        log.info("Cache MISS → Fetching ALL flights from FlightHotel Service");
        ResponseEntity<FlightDTO[]> response = restTemplate.getForEntity(url, FlightDTO[].class);
        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }

    @Cacheable(value = "search", key = "'all-hotels'")
    public List<HotelDTO> getAllHotels() {
        String url = flightHotelServiceUrl + "/hotels";
        log.info("Cache MISS → Fetching ALL hotels from FlightHotel Service");
        ResponseEntity<HotelDTO[]> response = restTemplate.getForEntity(url, HotelDTO[].class);
        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }
}
