package com.trip.messaging;

import com.trip.events.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentProducer {

    private final KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate;
    private final String topic = "payment_completed";

    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        kafkaTemplate.send(topic, String.valueOf(event.bookingId()), event);
        System.out.println("Payment completed event sent: " + event);
    }
}