package com.trip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class FlightHotelServicesApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlightHotelServicesApplication.class, args);

    }
}