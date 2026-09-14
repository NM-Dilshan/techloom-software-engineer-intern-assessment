package com.techloom.task01.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentDTO(@NotBlank String result, @NotBlank String idempotencyKey) {}