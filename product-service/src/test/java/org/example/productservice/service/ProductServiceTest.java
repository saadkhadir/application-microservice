package org.example.productservice.service;

import org.example.productservice.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductService Interface Tests")
class ProductServiceTest {

    private ProductService productService;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(99.99)
                .quantity(10)
                .build();
    }

    @Test
    @DisplayName("Should verify ProductService interface methods exist")
    void testProductServiceInterface() {
        // This test verifies that the interface has the expected methods
        assertTrue(hasMethod(ProductService.class, "findAll"));
        assertTrue(hasMethod(ProductService.class, "findById"));
        assertTrue(hasMethod(ProductService.class, "save"));
        assertTrue(hasMethod(ProductService.class, "update"));
        assertTrue(hasMethod(ProductService.class, "deleteById"));
    }

    @Test
    @DisplayName("Should verify method signatures")
    void testMethodSignatures() {
        assertDoesNotThrow(() -> {
            ProductService.class.getMethod("findAll");
            ProductService.class.getMethod("findById", Long.class);
            ProductService.class.getMethod("save", Product.class);
            ProductService.class.getMethod("update", Long.class, Product.class);
            ProductService.class.getMethod("deleteById", Long.class);
        });
    }

    // Helper method
    private boolean hasMethod(Class<?> clazz, String methodName) {
        return java.util.Arrays.stream(clazz.getMethods())
                .anyMatch(m -> m.getName().equals(methodName));
    }
}

