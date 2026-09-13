package com.techloom.task02.dto;

import com.techloom.task02.entity.Cart;
import java.math.BigDecimal;
import java.util.List;

public record CartDto(List<Item> items, BigDecimal total) {
    public record Item(Long productId, String name, BigDecimal price, Integer quantity, Integer stock) {}
    public static CartDto from(Cart cart) {
        List<Item> items = cart.getItems().stream().map(i -> new Item(i.getProduct().getId(), i.getProduct().getName(), i.getProduct().getPrice(), i.getQuantity(), i.getProduct().getStock())).toList();
        return new CartDto(items, items.stream().map(i -> i.price().multiply(BigDecimal.valueOf(i.quantity()))).reduce(BigDecimal.ZERO, BigDecimal::add));
    }
}