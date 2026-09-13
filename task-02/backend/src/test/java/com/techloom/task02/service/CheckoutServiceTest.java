package com.techloom.task02.service;

import com.techloom.task02.entity.*;
import com.techloom.task02.exception.ApiException;
import com.techloom.task02.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {
    @Mock CartRepository carts;
    @Mock ProductRepository products;
    @Mock OrderRepository orders;
    @Mock StockReservationRepository reservations;
    @InjectMocks CheckoutService service;

    @Test void checkoutRejectsInsufficientStock() {
        Product product = Product.builder().id(1L).name("Keyboard").price(new BigDecimal("10.00")).stock(0).build();
        Cart cart = Cart.builder().userId("u").items(new ArrayList<>()).build();
        cart.getItems().add(CartItem.builder().cart(cart).product(product).quantity(1).build());
        when(carts.findByUserId("u")).thenReturn(Optional.of(cart));
        when(products.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
        assertThrows(ApiException.class, () -> service.checkout("u"));
        verify(orders, never()).save(any());
    }

    @Test void paymentIsIdempotentAfterSuccess() {
        Order order = Order.builder().id(7L).status(OrderStatus.PAID).paymentStatus(PaymentStatus.SUCCESS).total(new BigDecimal("10.00")).items(new ArrayList<>()).build();
        when(orders.findById(7L)).thenReturn(Optional.of(order));
        var result = service.pay(7L, "FAILURE", "same-key");
        assertEquals(PaymentStatus.SUCCESS, result.paymentStatus());
        verify(orders, never()).save(any());
    }
}
