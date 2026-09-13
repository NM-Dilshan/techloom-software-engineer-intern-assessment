package com.techloom.task02.controller;
import com.techloom.task02.dto.ProductDto; import com.techloom.task02.service.ProductService; import lombok.RequiredArgsConstructor; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/products") @RequiredArgsConstructor
public class ProductController { private final ProductService service; @GetMapping public List<ProductDto> search(@RequestParam(required=false) String q,@RequestParam(required=false) String category){return service.search(q,category);} @GetMapping("/{id}") public ProductDto get(@PathVariable Long id){return service.get(id);} }
