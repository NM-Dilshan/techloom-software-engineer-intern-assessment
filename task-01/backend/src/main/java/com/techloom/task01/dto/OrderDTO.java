package com.techloom.task01.dto;

import com.techloom.task01.entity.*;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO(Long id, String userId, OrderStatus status, PaymentStatus paymentStatus, Double total, LocalDateTime createdAt, LocalDateTime reservationExpiresAt, List<Item> items) {
    public record Item(String name, Integer quantity, Double unitPrice) {}
    public static OrderDTO from(Order o) { return new OrderDTO(o.getId(), o.getUserId(), o.getStatus(), o.getPaymentStatus(), o.getTotal(), o.getCreatedAt(), o.getReservationExpiresAt(), o.getItems().stream().map(i -> new Item(i.getProduct().getName(), i.getQuantity(), i.getUnitPrice())).toList()); }
}