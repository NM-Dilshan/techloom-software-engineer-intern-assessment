package com.techloom.task02.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "orders")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String userId;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private OrderStatus status;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private PaymentStatus paymentStatus;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal total;
    @Column(nullable = false) private Instant createdAt;
    @Column(nullable = false) private Instant reservationExpiresAt;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default private List<OrderItem> items = new ArrayList<>();
}
