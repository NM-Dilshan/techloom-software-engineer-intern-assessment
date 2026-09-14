package com.techloom.task01.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional = false) private Order order;
    @ManyToOne(optional = false) private Product product;
    @Column(nullable = false) private Integer quantity;
    @Column(nullable = false) private Double unitPrice;
}