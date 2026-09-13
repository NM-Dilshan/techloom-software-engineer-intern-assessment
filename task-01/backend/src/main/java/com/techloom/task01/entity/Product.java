package com.techloom.task01.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name is required")
    @Column(nullable = false)
    private String name;

    @Positive(message = "Price must be positive")
    @Column(nullable = false)
    private Double price;

    /**
     * Available stock: quantity available for purchase.
     * availableStock + reservedStock = totalStock
     */
    @Column(nullable = false)
    private Long availableStock;

    /**
     * Reserved stock: quantity reserved by pending orders.
     */
    @Column(nullable = false)
    private Long reservedStock;

    /**
     * Version for optimistic locking to detect concurrent modifications.
     * Incremented on each update.
     */
    @Version
    private Long version;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        reservedStock = 0L;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Total stock is always availableStock + reservedStock.
     */
    public Long getTotalStock() {
        return availableStock + reservedStock;
    }
}
