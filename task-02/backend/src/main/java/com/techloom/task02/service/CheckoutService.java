package com.techloom.task02.service;

import com.techloom.task02.dto.*;
import com.techloom.task02.entity.*;
import com.techloom.task02.exception.ApiException;
import com.techloom.task02.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service @RequiredArgsConstructor
public class CheckoutService {
    private final CartRepository carts; private final ProductRepository products; private final OrderRepository orders; private final StockReservationRepository reservations;
    private Cart cart(String user) { return carts.findByUserId(user).orElseGet(() -> carts.save(Cart.builder().userId(user).build())); }
    @Transactional public CartDto getCart(String user) { return CartDto.from(cart(user)); }
    @Transactional public CartDto add(String user, Long productId, int quantity) { Cart c=cart(user); Product p=products.findById(productId).orElseThrow(() -> new ApiException("Product not found",HttpStatus.NOT_FOUND)); CartItem i=c.getItems().stream().filter(x -> x.getProduct().getId().equals(productId)).findFirst().orElse(null); if(i==null)c.getItems().add(CartItem.builder().cart(c).product(p).quantity(quantity).build()); else i.setQuantity(i.getQuantity()+quantity); return CartDto.from(carts.save(c)); }
    @Transactional public CartDto remove(String user, Long productId) { Cart c=cart(user); c.getItems().removeIf(i -> i.getProduct().getId().equals(productId)); return CartDto.from(carts.save(c)); }
    @Transactional public CheckoutResponse checkout(String user) { Cart c=cart(user); if(c.getItems().isEmpty()) throw new ApiException("Cart is empty",HttpStatus.BAD_REQUEST); Instant expiry=Instant.now().plusSeconds(300); Order o=Order.builder().userId(user).status(OrderStatus.PENDING_PAYMENT).paymentStatus(PaymentStatus.PENDING).createdAt(Instant.now()).reservationExpiresAt(expiry).total(BigDecimal.ZERO).build(); BigDecimal total=BigDecimal.ZERO; for(CartItem ci:c.getItems()){ Product p=products.findByIdForUpdate(ci.getProduct().getId()).orElseThrow(() -> new ApiException("Product not found",HttpStatus.NOT_FOUND)); if(p.getStock()<ci.getQuantity()) throw new ApiException("Insufficient stock for "+p.getName(),HttpStatus.CONFLICT); p.setStock(p.getStock()-ci.getQuantity()); total=total.add(p.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()))); o.getItems().add(OrderItem.builder().order(o).product(p).productName(p.getName()).quantity(ci.getQuantity()).unitPrice(p.getPrice()).build()); } o.setTotal(total); o=orders.save(o); for(OrderItem i:o.getItems()) reservations.save(StockReservation.builder().order(o).product(i.getProduct()).quantity(i.getQuantity()).expiresAt(expiry).active(true).build()); c.getItems().clear(); carts.save(c); return new CheckoutResponse(o.getId(),o.getTotal(),o.getStatus(),o.getPaymentStatus(),expiry); }
    @Transactional public OrderDto pay(Long id,String result,String key,String user) { Order o=owned(id,user); if(o.getPaymentIdempotencyKey()!=null && !o.getPaymentIdempotencyKey().equals(key)) throw new ApiException("A different payment key was already used",HttpStatus.CONFLICT); if(o.getPaymentStatus()!=PaymentStatus.PENDING)return OrderDto.from(o); if(Instant.now().isAfter(o.getReservationExpiresAt())) { expire(o); throw new ApiException("Reservation expired",HttpStatus.CONFLICT); } o.setPaymentIdempotencyKey(key); PaymentStatus ps=switch(result.toUpperCase(Locale.ROOT)){case "SUCCESS"->PaymentStatus.SUCCESS;case "TIMEOUT"->PaymentStatus.TIMEOUT;default->PaymentStatus.FAILURE;}; o.setPaymentStatus(ps); o.setStatus(ps==PaymentStatus.SUCCESS?OrderStatus.PAID:ps==PaymentStatus.TIMEOUT?OrderStatus.EXPIRED:OrderStatus.PAYMENT_FAILED); if(ps==PaymentStatus.SUCCESS) reservations.findByOrder(o).forEach(r -> r.setActive(false)); else release(o); return OrderDto.from(orders.save(o)); }
    @Transactional public OrderDto pay(Long id,String result,String key) { Order o=orders.findById(id).orElseThrow(() -> new ApiException("Order not found",HttpStatus.NOT_FOUND)); if(o.getPaymentStatus()!=PaymentStatus.PENDING)return OrderDto.from(o); return pay(id,result,key,o.getUserId()); }
    @Transactional public OrderDto cancel(Long id,String user) { Order o=owned(id,user); if(o.getStatus()==OrderStatus.CANCELLED)return OrderDto.from(o); if(o.getStatus()!=OrderStatus.PAID && o.getStatus()!=OrderStatus.PENDING_PAYMENT)throw new ApiException("Order cannot be cancelled",HttpStatus.CONFLICT); if(o.getStatus()==OrderStatus.PAID)o.setPaymentStatus(PaymentStatus.REFUNDED); else release(o); o.setStatus(OrderStatus.CANCELLED); return OrderDto.from(orders.save(o)); }
    @Transactional public List<OrderDto> history(String user){return orders.findByUserIdOrderByCreatedAtDesc(user).stream().map(OrderDto::from).toList();}
    @Transactional public OrderDto getOrder(Long id,String user){return OrderDto.from(owned(id,user));}
    private Order owned(Long id,String user){Order o=orders.findByIdForUpdate(id).orElseThrow(() -> new ApiException("Order not found",HttpStatus.NOT_FOUND)); if(!o.getUserId().equals(user))throw new ApiException("Forbidden",HttpStatus.FORBIDDEN); return o;}
    private void release(Order o){reservations.findByOrder(o).forEach(r->{if(r.isActive()){r.getProduct().setStock(r.getProduct().getStock()+r.getQuantity());r.setActive(false);}});}
    private void expire(Order o){release(o);o.setStatus(OrderStatus.EXPIRED);o.setPaymentStatus(PaymentStatus.TIMEOUT);}
    @Scheduled(fixedDelay=30000) @Transactional public void expireReservations(){reservations.findByActiveTrueAndExpiresAtBefore(Instant.now()).forEach(r->{if(r.getOrder().getPaymentStatus()==PaymentStatus.PENDING)expire(r.getOrder());});}
}
