package com.trip.dto;

import lombok.Data;

@Data
public class HotelDTO {
    private Long hotelId;
    private String name;
    private String city;
    private Double pricePerNight;
    private Integer stars;
}