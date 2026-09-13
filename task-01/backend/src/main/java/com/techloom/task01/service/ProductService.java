package com.techloom.task01.service;

import com.techloom.task01.dto.ProductDTO;
import com.techloom.task01.entity.Product;
import com.techloom.task01.exception.ProductNotFoundException;
import com.techloom.task01.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Create a new product.
     */
    public ProductDTO createProduct(ProductDTO dto) {
        log.info("Creating product: {}", dto.getName());
        Product product = Product.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .availableStock(dto.getAvailableStock())
                .reservedStock(0L)
                .build();
        Product saved = productRepository.save(product);
        return toDTO(saved);
    }

    /**
     * Get all products.
     */
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get a product by ID.
     */
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return toDTO(product);
    }

    /**
     * Update an existing product.
     * Note: Only name, price, and availableStock can be directly updated.
     * reservedStock and version are managed internally.
     */
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        log.info("Updating product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        // Only update availableStock if provided
        if (dto.getAvailableStock() != null && dto.getAvailableStock() >= 0) {
            product.setAvailableStock(dto.getAvailableStock());
        }

        Product updated = productRepository.save(product);
        return toDTO(updated);
    }

    /**
     * Delete a product by ID.
     */
    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        productRepository.delete(product);
    }

    /**
     * Convert Product entity to DTO.
     */
    private ProductDTO toDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .availableStock(product.getAvailableStock())
                .reservedStock(product.getReservedStock())
                .totalStock(product.getTotalStock())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .version(product.getVersion())
                .build();
    }
}
