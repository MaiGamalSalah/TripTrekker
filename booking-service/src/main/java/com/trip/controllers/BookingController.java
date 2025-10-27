package com.trip.controllers;

import com.trip.DTOs.CreateBookingRequest;
import com.trip.DTOs.CreateBookingResponse;
import com.trip.services.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}