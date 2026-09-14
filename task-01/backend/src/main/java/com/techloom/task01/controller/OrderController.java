package com.techloom.task01.controller;

import com.techloom.task01.dto.*;
import com.techloom.task01.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class OrderController {
    private final OrderService service;
    private String user(String value) { return value == null || value.isBlank() ? "demo-user" : value; }
    @PostMapping("/checkout") public OrderDTO checkout(@RequestHeader(value="X-User-Id", required=false) String user, @Valid @RequestBody List<CartItemDTO> cart) { return service.checkout(user(user), cart); }
    @PostMapping("/orders/{id}/payment") public OrderDTO pay(@PathVariable Long id, @RequestHeader(value="X-User-Id", required=false) String user, @Valid @RequestBody PaymentDTO payment) { return service.pay(id, payment.result(), payment.idempotencyKey(), user(user)); }
    @PostMapping("/orders/{id}/cancel") public OrderDTO cancel(@PathVariable Long id, @RequestHeader(value="X-User-Id", required=false) String user) { return service.cancel(id, user(user)); }
    @GetMapping("/orders") public List<OrderDTO> history(@RequestHeader(value="X-User-Id", required=false) String user) { return service.history(user(user)); }
    @GetMapping("/orders/{id}") public OrderDTO get(@PathVariable Long id, @RequestHeader(value="X-User-Id", required=false) String user) { return service.get(id, user(user)); }
}