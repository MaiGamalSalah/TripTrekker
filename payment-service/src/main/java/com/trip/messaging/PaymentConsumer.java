package com.trip.messaging;

import com.trip.entities.Payment;
import com.trip.events.BookingCreatedEvent;
import com.trip.events.PaymentCompletedEvent;
import com.trip.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final PaymentService paymentService;
    private final PaymentProducer paymentProducer;

    @KafkaListener(topics = "booking_created", groupId = "payment-group")
    public void consumeBookingCreatedEvent(BookingCreatedEvent event) {
        System.out.println("Received booking event: " + event);

        try {
            Long bookingId = event.bookingId();
            Double amount = event.amount();

            Payment payment = paymentService.processPayment(bookingId, amount);
            System.out.println("Payment processed: " + payment);

            PaymentCompletedEvent completedEvent = new PaymentCompletedEvent(bookingId, amount, payment.getStatus().name(), Instant.now());

            paymentProducer.publishPaymentCompleted(completedEvent);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
