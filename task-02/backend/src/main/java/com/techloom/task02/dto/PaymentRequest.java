package com.techloom.task02.dto;
import jakarta.validation.constraints.NotBlank;
public record PaymentRequest(@NotBlank String result, @NotBlank String idempotencyKey) {}
