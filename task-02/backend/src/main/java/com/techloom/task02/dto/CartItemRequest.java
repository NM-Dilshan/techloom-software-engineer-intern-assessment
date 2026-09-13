package com.techloom.task02.dto;
import jakarta.validation.constraints.Min;
public record CartItemRequest(Long productId, @Min(1) Integer quantity) {}
