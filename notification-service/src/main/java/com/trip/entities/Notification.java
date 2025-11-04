package com.trip.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   private String userId;
    private String message;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;
}

