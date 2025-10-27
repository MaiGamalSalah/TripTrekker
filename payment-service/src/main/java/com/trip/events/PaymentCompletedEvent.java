package com.trip.events;

import java.time.Instant;

public record PaymentCompletedEvent(
        Long bookingId,
        Double amount,
        String status, // SUCCESS or FAILED
        Instant processedAt
) {}