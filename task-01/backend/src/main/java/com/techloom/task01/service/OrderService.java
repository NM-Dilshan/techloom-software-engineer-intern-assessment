package com.techloom.task01.service;

import com.techloom.task01.dto.*;
import com.techloom.task01.entity.*;
import com.techloom.task01.exception.ProductNotFoundException;
import com.techloom.task01.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.*;

@Service @RequiredArgsConstructor
public class OrderService {
    private final ProductRepository products;
    private final OrderRepository orders;

    @Transactional
    public OrderDTO checkout(String userId, List<CartItemDTO> cart) {
        if (cart == null || cart.isEmpty()) throw bad("Cart is empty");
        Order order = Order.builder().userId(userId).status(OrderStatus.RESERVED).paymentStatus(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now()).reservationExpiresAt(LocalDateTime.now().plusMinutes(5)).total(0D).build();
        double total = 0;
        for (CartItemDTO request : cart) {
            Product product = products.findByIdForUpdate(request.productId()).orElseThrow(() -> new ProductNotFoundException(request.productId()));
            if (product.getAvailableStock() < request.quantity()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock for " + product.getName());
            product.setAvailableStock(product.getAvailableStock() - request.quantity());
            product.setReservedStock(product.getReservedStock() + request.quantity());
            total += product.getPrice() * request.quantity();
            order.getItems().add(OrderItem.builder().order(order).product(product).quantity(request.quantity()).unitPrice(product.getPrice()).build());
        }
        order.setTotal(total);
        return OrderDTO.from(orders.save(order));
    }

    @Transactional
    public OrderDTO pay(Long id, String result, String key, String userId) {
        Order order = owned(id, userId);
        if (order.getPaymentKey() != null && !order.getPaymentKey().equals(key)) throw new ResponseStatusException(HttpStatus.CONFLICT, "A different payment key was already used");
        if (order.getPaymentStatus() != PaymentStatus.PENDING) return OrderDTO.from(order);
        if (LocalDateTime.now().isAfter(order.getReservationExpiresAt())) { release(order); order.setStatus(OrderStatus.EXPIRED); order.setPaymentStatus(PaymentStatus.TIMEOUT); return OrderDTO.from(orders.save(order)); }
        order.setPaymentKey(key);
        PaymentStatus payment = switch (result.toUpperCase(Locale.ROOT)) { case "SUCCESS" -> PaymentStatus.SUCCESS; case "TIMEOUT" -> PaymentStatus.TIMEOUT; default -> PaymentStatus.FAILURE; };
        order.setPaymentStatus(payment);
        if (payment == PaymentStatus.SUCCESS) { order.setStatus(OrderStatus.PAID); confirm(order); }
        else { order.setStatus(payment == PaymentStatus.TIMEOUT ? OrderStatus.EXPIRED : OrderStatus.FAILED); release(order); }
        return OrderDTO.from(orders.save(order));
    }

    @Transactional public OrderDTO cancel(Long id, String userId) {
        Order order = owned(id, userId);
        if (order.getStatus() == OrderStatus.CANCELLED) return OrderDTO.from(order);
        if (order.getStatus() != OrderStatus.PAID && order.getStatus() != OrderStatus.RESERVED) throw bad("Order cannot be cancelled in its current state");
        if (order.getStatus() == OrderStatus.PAID) order.setPaymentStatus(PaymentStatus.REFUNDED); else release(order);
        order.setStatus(OrderStatus.CANCELLED);
        return OrderDTO.from(orders.save(order));
    }

    @Transactional(readOnly = true) public List<OrderDTO> history(String userId) { return orders.findByUserIdOrderByCreatedAtDesc(userId).stream().map(OrderDTO::from).toList(); }
    @Transactional(readOnly = true) public OrderDTO get(Long id, String userId) { return OrderDTO.from(owned(id, userId)); }

    @Scheduled(fixedDelay = 30000)
    @Transactional public void expireReservations() { orders.findAll().stream().filter(o -> o.getStatus() == OrderStatus.RESERVED && LocalDateTime.now().isAfter(o.getReservationExpiresAt())).forEach(o -> { release(o); o.setStatus(OrderStatus.EXPIRED); o.setPaymentStatus(PaymentStatus.TIMEOUT); }); }
    private Order owned(Long id, String userId) { Order order = orders.findByIdForUpdate(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found")); if (!order.getUserId().equals(userId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Order belongs to another user"); return order; }
    private void confirm(Order order) { order.getItems().forEach(i -> i.getProduct().setReservedStock(i.getProduct().getReservedStock() - i.getQuantity())); }
    private void release(Order order) { order.getItems().forEach(i -> { i.getProduct().setAvailableStock(i.getProduct().getAvailableStock() + i.getQuantity()); i.getProduct().setReservedStock(i.getProduct().getReservedStock() - i.getQuantity()); }); }
    private ResponseStatusException bad(String message) { return new ResponseStatusException(HttpStatus.BAD_REQUEST, message); }
}