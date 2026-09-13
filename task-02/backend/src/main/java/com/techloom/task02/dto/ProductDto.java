package com.techloom.task02.dto;
import com.techloom.task02.entity.Product;
import java.math.BigDecimal;
public record ProductDto(Long id, String name, String description, BigDecimal price, Integer stock, String category, String imageUrl) { public static ProductDto from(Product p) { return new ProductDto(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock(), p.getCategory(), p.getImageUrl()); } }
