package com.trip.dto;

import lombok.Data;

@Data
public class FlightDTO {
    private Long flightId;
    private String flightNumber;
    private String origin;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private Double price;
}