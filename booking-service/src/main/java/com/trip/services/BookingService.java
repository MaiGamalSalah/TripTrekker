package com.trip.services;

import com.trip.DTOs.CreateBookingRequest;
import com.trip.entities.Booking;
import com.trip.events.BookingCreatedEvent;
import com.trip.messaging.KafkaProducerService;
import com.trip.repositories.BookingRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RestTemplate restTemplate = new RestTemplate(); // or use WebClient/Feign
    private final KafkaProducerService kafkaProducerService;

    public BookingService(BookingRepository bookingRepository, KafkaProducerService kafkaProducerService) {
        this.bookingRepository = bookingRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    public Long createBooking(CreateBookingRequest req) {
        // 1. extract user id from JWT
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = null;
        if (principal instanceof Jwt jwt) {
            String sub = jwt.getSubject(); // UUID from Keycloak
            userId = sub;
        } else {
            throw new RuntimeException("Unauthenticated");
        }


//        // 2. optional: validate flight/hotel availability
//        if (req.flightId() != null) {
//            String flightUrl = "http://flight-service/api/flights/" + req.flightId() + "/available";
//            Boolean available = restTemplate.getForObject(flightUrl, Boolean.class);
//            if (available == null || !available) throw new RuntimeException("Flight not available");
//        }
//        if (req.hotelId() != null) {
//            String hotelUrl = "http://hotel-service/api/hotels/" + req.hotelId() + "/available";
//            Boolean available = restTemplate.getForObject(hotelUrl, Boolean.class);
//            if (available == null || !available) throw new RuntimeException("Hotel not available");
//        }

        // 3. persist booking with status PENDING
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setFlightId(req.flightId());
        booking.setHotelId(req.hotelId());
        booking.setBookingDate(Instant.now());
        booking.setStatus("PENDING");
        Booking saved = bookingRepository.save(booking);

        // 4. publish booking_created event for Payment / Notification
        BookingCreatedEvent event = new BookingCreatedEvent(
                saved.getBookingId(), saved.getUserId(), saved.getFlightId(), saved.getHotelId(), req.amount(), saved.getBookingDate()
        );
        kafkaProducerService.publishBookingCreated(event);

        return saved.getBookingId();
    }

    public void updateStatus(Long bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found with id " + bookingId));

        booking.setStatus(status);
        bookingRepository.save(booking);
    }
}