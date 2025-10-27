package com.trip.DTOs;

public record CreateBookingRequest(Long flightId, Long hotelId, Double amount) { }