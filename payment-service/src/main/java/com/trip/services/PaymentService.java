package com.trip.services;

import com.trip.entities.Payment;
import com.trip.entities.PaymentAccount;
import com.trip.entities.PaymentStatus;
import com.trip.events.PaymentCompletedEvent;
import com.trip.messaging.KafkaProducerService;
import com.trip.repositories.PaymentAccountRepository;
import com.trip.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentAccountRepository paymentAccountRepository;
    private final KafkaProducerService kafkaProducerService;

    @Transactional
    public Payment processPayment(Long bookingId, Double amount, String userId) throws InterruptedException {
        System.out.println(" [PaymentService] Starting payment process for user: " + userId + ", amount: " + amount);


        PaymentAccount account = paymentAccountRepository.findByUserId(userId)
                .orElseGet(() -> {
                    System.out.println("[PaymentService] No existing account found for user. Creating a new one...");
                    PaymentAccount newAcc = PaymentAccount.builder()
                            .userId(userId)
                            .balance(5000.0) // mock balance
                            .build();
                    return paymentAccountRepository.save(newAcc);
                });

        System.out.println(" [PaymentService] Current balance for user " + userId + ": " + account.getBalance());

        Thread.sleep(1000); // simulate payment gateway delay

        boolean isSuccess = false;

        //  لو الرصيد كافي
        if (account.getBalance() >= amount) {
            // خصم المبلغ
            account.setBalance(account.getBalance() - amount);
            paymentAccountRepository.save(account);
            isSuccess = true;
        }

        //  حفظ عملية الدفع في جدول payments
        Payment payment = Payment.builder()
                .bookingId(bookingId)
                .amount(amount)
                .status(isSuccess ? PaymentStatus.SUCCESS : PaymentStatus.FAILED)
                .createdAt(LocalDateTime.now())
                .build();

        paymentRepository.save(payment);

        //  نبعث الإيفنت بس لو الدفع نجح
        if (isSuccess) {
            PaymentCompletedEvent event = new PaymentCompletedEvent(
                    bookingId,
                    amount,
                    payment.getStatus().toString(),
                    Instant.now()
            );
            kafkaProducerService.publishPaymentCompleted(event);
        }
        System.out.println(" [PaymentService] PaymentCompletedEvent sent for bookingId: " + bookingId);
        System.out.println("------------------------------------------------------------");

        return payment;
    }
}
