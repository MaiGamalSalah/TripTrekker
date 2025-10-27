package com.trip.events;



import java.time.Instant;

public record BookingCreatedEvent(
        Long bookingId,
        String userId,
        Long flightId,
        Long hotelId,
        Double amount,
        Instant createdAt
) { }