package com.techloom.task01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    private Long id;

    @NotBlank(message = "Product name is required")
    private String name;

    @Positive(message = "Price must be positive")
    private Double price;

    @Positive(message = "Available stock must be positive")
    private Long availableStock;

    private Long reservedStock;

    private Long totalStock;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;
}
