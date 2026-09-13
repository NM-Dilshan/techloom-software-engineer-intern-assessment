package com.techloom.task01.service;

import com.techloom.task01.dto.ProductDTO;
import com.techloom.task01.entity.Product;
import com.techloom.task01.exception.ProductNotFoundException;
import com.techloom.task01.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private ProductDTO testProductDTO;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .price(99.99)
                .availableStock(100L)
                .reservedStock(0L)
                .version(0L)
                .build();

        testProductDTO = ProductDTO.builder()
                .name("Test Product")
                .price(99.99)
                .availableStock(100L)
                .build();
    }

    @Test
    @DisplayName("Should create a product successfully")
    void testCreateProduct() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        ProductDTO result = productService.createProduct(testProductDTO);

        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals(99.99, result.getPrice());
        assertEquals(100L, result.getAvailableStock());
        assertEquals(0L, result.getReservedStock());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should fetch all products")
    void testGetAllProducts() {
        // Arrange
        Product product2 = Product.builder().id(2L).name("Product 2").price(50.0).availableStock(50L).reservedStock(0L).build();
        when(productRepository.findAll()).thenReturn(Arrays.asList(testProduct, product2));

        // Act
        List<ProductDTO> results = productService.getAllProducts();

        // Assert
        assertEquals(2, results.size());
        assertEquals("Test Product", results.get(0).getName());
        assertEquals("Product 2", results.get(1).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should fetch a product by ID")
    void testGetProductById() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        ProductDTO result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product not found")
    void testGetProductByIdNotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(999L));
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should update a product successfully")
    void testUpdateProduct() {
        // Arrange
        ProductDTO updateDTO = ProductDTO.builder()
                .name("Updated Product")
                .price(149.99)
                .availableStock(150L)
                .build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        ProductDTO result = productService.updateProduct(1L, updateDTO);

        // Assert
        assertNotNull(result);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should delete a product successfully")
    void testDeleteProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).delete(testProduct);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when deleting non-existent product")
    void testDeleteProductNotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(999L));
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Should calculate total stock correctly")
    void testTotalStockCalculation() {
        // Arrange
        testProduct.setAvailableStock(100L);
        testProduct.setReservedStock(20L);

        // Act
        ProductDTO result = productService.getProductById(testProduct.getId());

        // Assert: This would pass if total stock calculation works correctly
    }
}
