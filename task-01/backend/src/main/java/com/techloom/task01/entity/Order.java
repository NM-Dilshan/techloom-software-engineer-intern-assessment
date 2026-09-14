package com.techloom.task01.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity @Table(name = "orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String userId;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private OrderStatus status;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private PaymentStatus paymentStatus;
    @Column(nullable = false) private Double total;
    @Column(nullable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private LocalDateTime reservationExpiresAt;
    @Column(unique = true) private String paymentKey;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default private List<OrderItem> items = new ArrayList<>();
}