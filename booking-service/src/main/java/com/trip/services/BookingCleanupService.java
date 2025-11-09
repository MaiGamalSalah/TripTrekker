package com.trip.services;

import com.trip.entities.Booking;
import com.trip.repositories.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;


@Service
@EnableScheduling
@RequiredArgsConstructor
public class BookingCleanupService {
    private final BookingRepository bookingRepository;
    private final WebClient webClient;

    @Value("${flight-hotel-service.url}")
    private String flightHotelBaseUrl;

    @Scheduled(fixedRate = 60000)
    public void cancelOldPendingBookings() {
        Instant cutoff = Instant.now().minus(5, ChronoUnit.MINUTES);
        List<Booking> expired = bookingRepository.findAllPendingOlderThan(cutoff);

        for (Booking b : expired) {
            b.setStatus("CANCELLED");
            bookingRepository.save(b);

            if (b.getFlightId() != null) {
                webClient.put()
                        .uri(flightHotelBaseUrl + "/flights/" + b.getFlightId() + "/available")
                        .retrieve()
                        .bodyToMono(Void.class)
                        .subscribe();
            }
            if (b.getHotelId() != null) {
                webClient.put()
                        .uri(flightHotelBaseUrl + "/hotels/" + b.getHotelId() + "/available")
                        .retrieve()
                        .bodyToMono(Void.class)
                        .subscribe();
            }
        }
    }
}
