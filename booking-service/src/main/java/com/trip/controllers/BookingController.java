package com.trip.controllers;

import com.trip.DTOs.CreateBookingRequest;
import com.trip.DTOs.CreateBookingResponse;
import com.trip.services.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;
    public BookingController(BookingService bookingService) { this.bookingService = bookingService; }

    @PostMapping
    public ResponseEntity<CreateBookingResponse> create(@RequestBody CreateBookingRequest req) {
        Long id = bookingService.createBooking(req);
        return ResponseEntity.ok(new CreateBookingResponse(id, "PENDING"));
    }
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<String> cancelBooking(@PathVariable Long bookingId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Jwt jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwt.getTokenValue();
        bookingService.cancelBooking(bookingId, token);

        return ResponseEntity.ok("Booking " + bookingId + " cancelled successfully.");
    }

}