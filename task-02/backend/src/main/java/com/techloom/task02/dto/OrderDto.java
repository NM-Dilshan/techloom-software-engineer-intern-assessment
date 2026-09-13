package com.techloom.task02.dto;
import com.techloom.task02.entity.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
public record OrderDto(Long id, BigDecimal total, OrderStatus status, PaymentStatus paymentStatus, Instant createdAt, Instant reservationExpiresAt, List<Item> items) { public record Item(String name, Integer quantity, BigDecimal unitPrice) {} public static OrderDto from(Order o) { return new OrderDto(o.getId(), o.getTotal(), o.getStatus(), o.getPaymentStatus(), o.getCreatedAt(), o.getReservationExpiresAt(), o.getItems().stream().map(i -> new Item(i.getProductName(), i.getQuantity(), i.getUnitPrice())).toList()); } }
