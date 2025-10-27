package com.trip.messaging;

import com.trip.events.PaymentCompletedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate;
    private final String topic = "payment_completed";

    public KafkaProducerService(KafkaTemplate<String, PaymentCompletedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        kafkaTemplate.send(topic, String.valueOf(event.bookingId()), event);
        System.out.println("PaymentCompletedEvent sent: " + event);
    }
}
