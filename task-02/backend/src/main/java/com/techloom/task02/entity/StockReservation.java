package com.techloom.task02.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StockReservation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) private Order order;
    @ManyToOne(optional = false) private Product product;
    @Column(nullable = false) private Integer quantity;
    @Column(nullable = false) private Instant expiresAt;
    @Column(nullable = false) private boolean active;
}
