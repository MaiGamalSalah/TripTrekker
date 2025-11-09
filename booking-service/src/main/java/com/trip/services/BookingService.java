package com.trip.services;

import com.trip.DTOs.CreateBookingRequest;
import com.trip.entities.Booking;
import com.trip.events.BookingCreatedEvent;
import com.trip.messaging.KafkaProducerService;
import com.trip.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;

@Service
public class BookingService {

    @Value("${flight-hotel-service.url}")
    private String flightHotelBaseUrl;
    private final WebClient webClient;
    private final BookingRepository bookingRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final KafkaProducerService kafkaProducerService;

    public BookingService(BookingRepository bookingRepository, KafkaProducerService kafkaProducerService,WebClient webClient) {
        this.bookingRepository = bookingRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.webClient = webClient;
    }

    public Long createBooking(CreateBookingRequest req) {
        // 1. extract user info & token
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId;
        String token;

        if (principal instanceof Jwt jwt) {
            userId = jwt.getSubject();
            token = jwt.getTokenValue();
        } else {
            throw new RuntimeException("Unauthenticated");
        }

        // 2. validate flight
        if (req.flightId() != null) {
            validateResource(token, flightHotelBaseUrl + "/flights/" + req.flightId(), "Flight");
        }

        // 3. validate hotel
        if (req.hotelId() != null) {
            validateResource(token, flightHotelBaseUrl + "/hotels/" + req.hotelId(), "Hotel");
        }

        // 4. save booking
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setFlightId(req.flightId());
        booking.setHotelId(req.hotelId());
        booking.setBookingDate(Instant.now());
        booking.setStatus("PENDING");

        Booking saved = bookingRepository.save(booking);

        long bookingsCount = bookingRepository.countByFlightId(req.flightId());
        if (bookingsCount >= 4) { //5
            webClient.put()
                    .uri("http://localhost:8083/flights/{flightId}/available", req.flightId())
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(response -> System.out.println("Flight availability updated: " + response));
        }
        if (bookingsCount >= 5) {
            webClient.put()
                    .uri("http://localhost:8083/flights/{flightId}/unavailable", req.flightId())
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(response -> System.out.println("Flight set unavailable: " + response));
        }
        if (req.hotelId() != null) {
            webClient.put()
                    .uri("http://localhost:8083/hotels/{hotelId}/unavailable", req.hotelId()) //  unavailable
                    .retrieve()
                    .bodyToMono(String.class)
                    .subscribe(response ->
                            System.out.println("Hotel " + req.hotelId() + " marked unavailable: " + response)
                    );
        }
        // 5. publish event
        BookingCreatedEvent event = new BookingCreatedEvent(
                saved.getBookingId(), saved.getUserId(), saved.getFlightId(),
                saved.getHotelId(), req.amount(), saved.getBookingDate()
        );
        kafkaProducerService.publishBookingCreated(event);

        return saved.getBookingId();
    }

    private void validateResource(String token, String url, String type) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException(type + " not available or unauthorized");
        }
    }

    public void updateStatus(Long bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id " + bookingId));
        booking.setStatus(status);
        bookingRepository.save(booking);
    }
    public void cancelBooking(Long bookingId, String token) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id " + bookingId));

        //  غير حالة الحجز
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);

        //  لو الفندق كان متحجز، رجّعه متاح
        if (booking.getHotelId() != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            String url = flightHotelBaseUrl + "/hotels/" + booking.getHotelId() + "/available";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

            System.out.println("Hotel availability restored: " + response.getBody());
        }

        System.out.println("Booking " + bookingId + " cancelled successfully.");
    }

}
