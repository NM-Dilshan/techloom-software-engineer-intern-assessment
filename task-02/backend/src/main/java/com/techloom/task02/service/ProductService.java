package com.techloom.task02.service;
import com.techloom.task02.dto.ProductDto;
import com.techloom.task02.entity.Product;
import com.techloom.task02.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*; import java.math.BigDecimal;
@Service @RequiredArgsConstructor
public class ProductService { private final ProductRepository repo; public List<ProductDto> search(String q, String category, BigDecimal min, BigDecimal max, boolean availableOnly) { return repo.findByNameContainingIgnoreCaseAndCategoryContainingIgnoreCase(q == null ? "" : q, category == null ? "" : category).stream().filter(p -> min == null || p.getPrice().compareTo(min) >= 0).filter(p -> max == null || p.getPrice().compareTo(max) <= 0).filter(p -> !availableOnly || p.getStock() > 0).map(ProductDto::from).toList(); } public ProductDto get(Long id) { return ProductDto.from(repo.findById(id).orElseThrow(() -> new com.techloom.task02.exception.ApiException("Product not found", org.springframework.http.HttpStatus.NOT_FOUND))); } }
