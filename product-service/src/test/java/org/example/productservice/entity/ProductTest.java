package org.example.productservice.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("High performance laptop")
                .price(999.99)
                .quantity(10)
                .build();
    }

    @Test
    void testProductCreation() {
        assertNotNull(product);
        assertEquals(1L, product.getId());
        assertEquals("Laptop", product.getName());
        assertEquals("High performance laptop", product.getDescription());
        assertEquals(999.99, product.getPrice());
        assertEquals(10, product.getQuantity());
    }

    @Test
    void testProductSetters() {
        product.setName("Desktop");
        product.setPrice(1299.99);
        product.setQuantity(5);

        assertEquals("Desktop", product.getName());
        assertEquals(1299.99, product.getPrice());
        assertEquals(5, product.getQuantity());
    }

    @Test
    void testProductBuilder() {
        Product builtProduct = Product.builder()
                .id(2L)
                .name("Mouse")
                .description("Wireless mouse")
                .price(29.99)
                .quantity(50)
                .build();

        assertEquals(2L, builtProduct.getId());
        assertEquals("Mouse", builtProduct.getName());
        assertEquals("Wireless mouse", builtProduct.getDescription());
        assertEquals(29.99, builtProduct.getPrice());
        assertEquals(50, builtProduct.getQuantity());
    }

    @Test
    void testProductNoArgsConstructor() {
        Product emptyProduct = new Product();
        assertNotNull(emptyProduct);
    }

    @Test
    void testProductAllArgsConstructor() {
        Product constructedProduct = new Product(3L, "Keyboard", "Mechanical keyboard", 79.99, 25);

        assertEquals(3L, constructedProduct.getId());
        assertEquals("Keyboard", constructedProduct.getName());
        assertEquals("Mechanical keyboard", constructedProduct.getDescription());
        assertEquals(79.99, constructedProduct.getPrice());
        assertEquals(25, constructedProduct.getQuantity());
    }
}

