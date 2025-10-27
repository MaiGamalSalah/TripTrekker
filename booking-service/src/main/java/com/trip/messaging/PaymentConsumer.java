package com.trip.messaging;

import com.trip.entities.Booking;
import com.trip.events.PaymentCompletedEvent;
import com.trip.repositories.BookingRepository;
import com.trip.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    @KafkaListener(topics = "payment_completed", groupId = "booking-group")
    public void consumePaymentCompletedEvent(PaymentCompletedEvent event) {
        System.out.println("Received payment event: " + event);

        try {
            Optional<Booking> bookingOpt = bookingRepository.findById(event.bookingId());
            if (bookingOpt.isPresent()) {
                Booking booking = bookingOpt.get();

                if ("SUCCESS".equalsIgnoreCase(event.status())) {
                    booking.setStatus("CONFIRMED");
                } else {
                    booking.setStatus("FAILED");
                }

                bookingRepository.save(booking);
                System.out.println("Booking updated: " +  booking.getStatus());
            } else {
                System.err.println("Booking not found for ID: " + event.bookingId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public void consumePaymentCompleted(PaymentCompletedEvent event) {
//        System.out.println("Received payment event: " + event);
//        //bookingService.updateStatus(event.bookingId(), "CONFIRMED");
//        String newStatus = event.status().equals("SUCCESS") ? "CONFIRMED" : "FAILED";
//        bookingService.updateStatus(event.bookingId(), newStatus);
//        System.out.println(" Booking status updated to: " + newStatus);
//    }

}