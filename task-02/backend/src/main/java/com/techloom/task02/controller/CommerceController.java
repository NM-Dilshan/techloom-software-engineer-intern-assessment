package com.techloom.task02.controller;
import com.techloom.task02.dto.*; import com.techloom.task02.service.CheckoutService; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api") @RequiredArgsConstructor
public class CommerceController { private final CheckoutService service; private String user(String id){return id==null||id.isBlank()?"demo-user":id;}
 @GetMapping("/cart") public CartDto cart(@RequestHeader(value="X-User-Id",required=false) String u){return service.getCart(user(u));}
 @PostMapping("/cart/items") public CartDto add(@RequestHeader(value="X-User-Id",required=false) String u,@Valid @RequestBody CartItemRequest r){return service.add(user(u),r.productId(),r.quantity());}
 @DeleteMapping("/cart/items/{productId}") public CartDto remove(@RequestHeader(value="X-User-Id",required=false) String u,@PathVariable Long productId){return service.remove(user(u),productId);}
 @PostMapping("/checkout") public CheckoutResponse checkout(@RequestHeader(value="X-User-Id",required=false) String u){return service.checkout(user(u));}
 @PostMapping("/orders/{id}/payment") public OrderDto pay(@PathVariable Long id,@RequestHeader(value="X-User-Id",required=false) String u,@Valid @RequestBody PaymentRequest r){return service.pay(id,r.result(),r.idempotencyKey(),user(u));}
 @PostMapping("/orders/{id}/cancel") public OrderDto cancel(@PathVariable Long id,@RequestHeader(value="X-User-Id",required=false) String u){return service.cancel(id,user(u));}
 @GetMapping("/orders") public List<OrderDto> orders(@RequestHeader(value="X-User-Id",required=false) String u){return service.history(user(u));}
 @GetMapping("/orders/{id}") public OrderDto order(@PathVariable Long id,@RequestHeader(value="X-User-Id",required=false) String u){return service.getOrder(id,user(u));}
}
