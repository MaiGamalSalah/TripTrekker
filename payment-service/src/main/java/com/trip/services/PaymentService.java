package com.trip.services;

import com.trip.entities.Payment;
import com.trip.entities.PaymentStatus;
import com.trip.events.PaymentCompletedEvent;
import com.trip.messaging.KafkaProducerService;
import com.trip.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Random;

@Service

public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaProducerService kafkaProducerService;

    public PaymentService(PaymentRepository paymentRepository, KafkaProducerService kafkaProducerService) {
        this.paymentRepository = paymentRepository;
        this.kafkaProducerService = kafkaProducerService;
    }

    public Payment processPayment(Long bookingId, Double amount) throws InterruptedException {
        // mock payment gateway


        //boolean isSuccess = new Random().nextBoolean();
        //boolean isSuccess = true;
        //boolean isSuccess = Math.random() > 0.2;


        boolean isSuccess;
        if (amount < 5000) {
            isSuccess = Math.random() > 0.1;
        } else {
            isSuccess = Math.random() > 0.5;
        }

        Thread.sleep(1000); // simulate payment gateway delay



        Payment payment = Payment.builder()
                .bookingId(bookingId)
                .amount(amount)
                .status(isSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .createdAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        PaymentCompletedEvent event = new PaymentCompletedEvent(
                bookingId,
                amount,
                payment.getStatus().toString(),
                Instant.now()
        );
        kafkaProducerService.publishPaymentCompleted(event);

        return payment;
    }

}