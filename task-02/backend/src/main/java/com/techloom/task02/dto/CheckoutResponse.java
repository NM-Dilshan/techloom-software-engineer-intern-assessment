package com.techloom.task02.dto;
import com.techloom.task02.entity.*;
import java.math.BigDecimal;
import java.time.Instant;
public record CheckoutResponse(Long orderId, BigDecimal total, OrderStatus status, PaymentStatus paymentStatus, Instant reservationExpiresAt) {}
