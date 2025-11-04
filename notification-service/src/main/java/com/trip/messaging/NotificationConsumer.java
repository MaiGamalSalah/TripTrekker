package com.trip.messaging;

import com.trip.entities.Notification;
import com.trip.entities.NotificationStatus;
import com.trip.events.PaymentCompletedEvent;
import com.trip.repositories.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {
    private final NotificationRepository notificationRepository;


    @KafkaListener(topics = "payment_completed", groupId = "notification-group")
    public void consume(PaymentCompletedEvent event) {
        System.out.println(" Received payment event: " + event);

//        if ("SUCCESS".equals(event.status())) {
//            System.out.println("Sending CONFIRMATION email for booking " + event.bookingId());
//        } else {
//            System.out.println("Sending FAILURE email for booking " + event.bookingId());
//        }

        String message = event.status().equals("SUCCESS")
                ? "Sending CONFIRMATION email for booking " + event.bookingId() + "CONFIRMATION "
                : "Sending FAILURE email for booking  " + event.bookingId() + " CONFIRMATION";

        Notification notification = Notification.builder()
                .userId("unknown") //  event.userid
                .message(message)
                .createdAt(LocalDateTime.now())
                .status(NotificationStatus.UNREAD)
                .build();

        notificationRepository.save(notification);

        System.out.println("Notification saved: " + message);
    }
}