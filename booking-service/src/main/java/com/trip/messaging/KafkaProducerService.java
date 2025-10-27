package com.trip.messaging;



import com.trip.events.BookingCreatedEvent;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate;
    private final String topic = "booking_created";

    public KafkaProducerService(KafkaTemplate<String, BookingCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishBookingCreated(BookingCreatedEvent event) {
        kafkaTemplate.send(topic, String.valueOf(event.bookingId()), event);
        System.out.println(" PaymentCompletedEvent sent to Kafka: " + event);
    }
}