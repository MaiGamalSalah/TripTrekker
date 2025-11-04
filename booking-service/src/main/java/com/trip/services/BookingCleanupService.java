package com.trip.services;

import com.trip.entities.Booking;
import com.trip.repositories.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(BookingCleanupService.class);
    private final BookingRepository bookingRepository;
    private final WebClient webClient;

    private static final String FLIGHT_SERVICE_URL = "http://localhost:8083/flights";
    private static final String HOTEL_SERVICE_URL = "http://localhost:8083/hotels";

    @Scheduled(fixedRate = 60000)
    public void cancelOldPendingBookings() {
        Instant cutoff = Instant.now().minus(5, ChronoUnit.MINUTES);
        List<Booking> expiredBookings = bookingRepository.findAllPendingOlderThan(cutoff);

        if (expiredBookings.isEmpty()) return;

        log.info("Found {} pending bookings older than 5 minutes. Cancelling...", expiredBookings.size());

        for (Booking booking : expiredBookings) {
            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);

            try {
                if (booking.getFlightId() != null) {
                    webClient.put()
                            .uri(FLIGHT_SERVICE_URL + "/" + booking.getFlightId() + "/available")
                            .retrieve()
                            .bodyToMono(Void.class)
                            .onErrorResume(e -> {
                                log.error("Failed to update flight availability", e);
                                return Mono.empty();
                            })
                            .block();
                }

                if (booking.getHotelId() != null) {
                    webClient.put()
                            .uri(HOTEL_SERVICE_URL + "/" + booking.getHotelId() + "/available")
                            .retrieve()
                            .bodyToMono(Void.class)
                            .onErrorResume(e -> {
                                log.error("Failed to update hotel availability", e);
                                return Mono.empty();
                            })
                            .block();
                }
            } catch (Exception e) {
                log.error("Error releasing resources for booking {}", booking.getBookingId(), e);
            }
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    public void deleteOldFlights() {
        webClient.delete()
                .uri(FLIGHT_SERVICE_URL + "/cleanup-old")
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(v -> log.info("Old flights cleanup triggered successfully."))
                .doOnError(e -> log.error("Failed to clean up old flights", e))
                .subscribe();
    }
}
