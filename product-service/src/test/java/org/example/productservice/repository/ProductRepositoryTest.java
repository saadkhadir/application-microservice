package org.example.productservice.repository;

import org.example.productservice.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductRepository Tests")
@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = Product.builder()
                .name("Laptop")
                .description("High performance laptop")
                .price(999.99)
                .quantity(10)
                .build();

        product2 = Product.builder()
                .name("Mouse")
                .description("Wireless mouse")
                .price(29.99)
                .quantity(50)
                .build();
    }

    @Test
    @DisplayName("Should save product successfully")
    void testSaveProduct() {
        Product savedProduct = productRepository.save(product1);

        assertNotNull(savedProduct.getId());
        assertEquals("Laptop", savedProduct.getName());
        assertEquals(999.99, savedProduct.getPrice());
    }

    @Test
    @DisplayName("Should find product by ID successfully")
    void testFindById() {
        Product savedProduct = productRepository.save(product1);
        entityManager.flush();

        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());

        assertTrue(foundProduct.isPresent());
        assertEquals("Laptop", foundProduct.get().getName());
        assertEquals("High performance laptop", foundProduct.get().getDescription());
    }

    @Test
    @DisplayName("Should return empty Optional when product not found")
    void testFindByIdNotFound() {
        Optional<Product> foundProduct = productRepository.findById(999L);
        assertFalse(foundProduct.isPresent());
    }

    @Test
    @DisplayName("Should find all products successfully")
    void testFindAll() {
        productRepository.save(product1);
        productRepository.save(product2);
        entityManager.flush();

        List<Product> allProducts = productRepository.findAll();

        assertEquals(2, allProducts.size());
    }

    @Test
    @DisplayName("Should return empty list when database is empty")
    void testFindAllEmpty() {
        List<Product> allProducts = productRepository.findAll();
        assertTrue(allProducts.isEmpty());
    }

    @Test
    @DisplayName("Should update product successfully")
    void testUpdateProduct() {
        Product savedProduct = productRepository.save(product1);
        entityManager.flush();

        savedProduct.setName("Updated Laptop");
        savedProduct.setPrice(1199.99);
        Product updatedProduct = productRepository.save(savedProduct);

        assertEquals("Updated Laptop", updatedProduct.getName());
        assertEquals(1199.99, updatedProduct.getPrice());
    }

    @Test
    @DisplayName("Should delete product by ID successfully")
    void testDeleteById() {
        Product savedProduct = productRepository.save(product1);
        entityManager.flush();

        Long productId = savedProduct.getId();
        productRepository.deleteById(productId);
        entityManager.flush();

        Optional<Product> deletedProduct = productRepository.findById(productId);
        assertFalse(deletedProduct.isPresent());
    }

    @Test
    @DisplayName("Should not throw exception when deleting non-existent product")
    void testDeleteByIdNonExistent() {
        assertDoesNotThrow(() -> productRepository.deleteById(999L));
    }

    @Test
    @DisplayName("Should save multiple products successfully")
    void testSaveMultipleProducts() {
        productRepository.save(product1);
        productRepository.save(product2);
        entityManager.flush();

        List<Product> allProducts = productRepository.findAll();
        assertEquals(2, allProducts.size());

        assertTrue(allProducts.stream().anyMatch(p -> p.getName().equals("Laptop")));
        assertTrue(allProducts.stream().anyMatch(p -> p.getName().equals("Mouse")));
    }

    @Test
    @DisplayName("Should calculate price range from products")
    void testProductPriceRange() {
        productRepository.save(product1);
        productRepository.save(product2);
        entityManager.flush();

        List<Product> allProducts = productRepository.findAll();

        double minPrice = allProducts.stream()
                .mapToDouble(Product::getPrice)
                .min()
                .orElse(0);

        double maxPrice = allProducts.stream()
                .mapToDouble(Product::getPrice)
                .max()
                .orElse(0);

        assertEquals(29.99, minPrice);
        assertEquals(999.99, maxPrice);
    }

    @Test
    @DisplayName("Should verify product persistence across transactions")
    void testProductPersistence() {
        Product savedProduct = productRepository.save(product1);
        Long productId = savedProduct.getId();
        entityManager.flush();
        entityManager.clear();

        Optional<Product> retrievedProduct = productRepository.findById(productId);

        assertTrue(retrievedProduct.isPresent());
        assertEquals("Laptop", retrievedProduct.get().getName());
        assertEquals(999.99, retrievedProduct.get().getPrice());
    }

    @Test
    @DisplayName("Should update product fields individually")
    void testPartialProductUpdate() {
        Product savedProduct = productRepository.save(product1);
        entityManager.flush();

        savedProduct.setQuantity(20);
        Product updatedProduct = productRepository.save(savedProduct);

        assertEquals(20, updatedProduct.getQuantity());
        assertEquals("Laptop", updatedProduct.getName());
        assertEquals("High performance laptop", updatedProduct.getDescription());
    }

    @Test
    @DisplayName("Should handle bulk save operations")
    void testBulkSave() {
        Product product3 = Product.builder()
                .name("Keyboard")
                .description("Mechanical keyboard")
                .price(79.99)
                .quantity(30)
                .build();

        List<Product> products = Arrays.asList(product1, product2, product3);
        productRepository.saveAll(products);
        entityManager.flush();

        List<Product> allProducts = productRepository.findAll();
        assertEquals(3, allProducts.size());
    }

    @Test
    @DisplayName("Should verify ID generation after save")
    void testIdGeneration() {
        assertNull(product1.getId());

        Product savedProduct = productRepository.save(product1);

        assertNotNull(savedProduct.getId());
        assertNotEquals(0L, savedProduct.getId());
    }
}

