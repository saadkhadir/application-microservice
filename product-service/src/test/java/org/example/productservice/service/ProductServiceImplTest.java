package org.example.productservice.service;

import org.example.productservice.entity.Product;
import org.example.productservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
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
@DisplayName("ProductServiceImpl Tests")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product1;
    private Product product2;
    private Product product3;

    @BeforeEach
    void setUp() {
        product1 = Product.builder()
                .id(1L)
                .name("Laptop")
                .description("High performance laptop")
                .price(999.99)
                .quantity(10)
                .build();

        product2 = Product.builder()
                .id(2L)
                .name("Mouse")
                .description("Wireless mouse")
                .price(29.99)
                .quantity(50)
                .build();

        product3 = Product.builder()
                .id(3L)
                .name("Keyboard")
                .description("Mechanical keyboard")
                .price(79.99)
                .quantity(25)
                .build();
    }

    // ==================== Tests for findAll() ====================

    @Test
    @DisplayName("Should return all products when database contains multiple products")
    void testFindAll_Success() {
        List<Product> expectedProducts = Arrays.asList(product1, product2, product3);
        when(productRepository.findAll()).thenReturn(expectedProducts);

        List<Product> actualProducts = productService.findAll();

        assertEquals(3, actualProducts.size());
        assertEquals("Laptop", actualProducts.get(0).getName());
        assertEquals("Mouse", actualProducts.get(1).getName());
        assertEquals("Keyboard", actualProducts.get(2).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void testFindAll_EmptyList() {
        when(productRepository.findAll()).thenReturn(Arrays.asList());

        List<Product> actualProducts = productService.findAll();

        assertTrue(actualProducts.isEmpty());
        assertEquals(0, actualProducts.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return list with one product")
    void testFindAll_SingleProduct() {
        List<Product> expectedProducts = Arrays.asList(product1);
        when(productRepository.findAll()).thenReturn(expectedProducts);

        List<Product> actualProducts = productService.findAll();

        assertEquals(1, actualProducts.size());
        assertEquals(product1, actualProducts.get(0));
        verify(productRepository, times(1)).findAll();
    }

    // ==================== Tests for findById() ====================

    @Test
    @DisplayName("Should return product when ID exists")
    void testFindById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        Product actualProduct = productService.findById(1L);

        assertNotNull(actualProduct);
        assertEquals(1L, actualProduct.getId());
        assertEquals("Laptop", actualProduct.getName());
        assertEquals(999.99, actualProduct.getPrice());
        assertEquals(10, actualProduct.getQuantity());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw RuntimeException when product not found")
    void testFindById_NotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.findById(999L));

        assertEquals("Product not found with id: 999", exception.getMessage());
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should throw exception for null ID")
    void testFindById_NullId() {
        when(productRepository.findById(null)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.findById(null));
        verify(productRepository, times(1)).findById(null);
    }

    // ==================== Tests for save() ====================

    @Test
    @DisplayName("Should save product successfully")
    void testSave_Success() {
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        productService.save(product1);

        verify(productRepository, times(1)).save(product1);
    }

    @Test
    @DisplayName("Should save new product with correct properties")
    void testSave_NewProduct() {
        Product newProduct = Product.builder()
                .name("Monitor")
                .description("4K Monitor")
                .price(399.99)
                .quantity(15)
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(newProduct);

        productService.save(newProduct);

        verify(productRepository, times(1)).save(newProduct);
    }

    @Test
    @DisplayName("Should handle null product in save")
    void testSave_NullProduct() {
        productService.save(null);
        verify(productRepository, times(1)).save(null);
    }

    // ==================== Tests for update() ====================

    @Test
    @DisplayName("Should update product successfully with all fields")
    void testUpdate_Success_AllFields() {
        Product updatedProduct = Product.builder()
                .name("Updated Laptop")
                .description("Updated description")
                .price(1199.99)
                .quantity(15)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        productService.update(1L, updatedProduct);

        assertEquals("Updated Laptop", product1.getName());
        assertEquals("Updated description", product1.getDescription());
        assertEquals(1199.99, product1.getPrice());
        assertEquals(15, product1.getQuantity());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should update only name field when others are null")
    void testUpdate_PartialUpdate_NameOnly() {
        Product partialUpdate = Product.builder()
                .name("New Name")
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        productService.update(1L, partialUpdate);

        assertEquals("New Name", product1.getName());
        assertEquals("High performance laptop", product1.getDescription());
        assertEquals(999.99, product1.getPrice());
        assertEquals(10, product1.getQuantity());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should update only price field when others are null")
    void testUpdate_PartialUpdate_PriceOnly() {
        Product partialUpdate = Product.builder()
                .price(1299.99)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        productService.update(1L, partialUpdate);

        assertEquals("Laptop", product1.getName());
        assertEquals("High performance laptop", product1.getDescription());
        assertEquals(1299.99, product1.getPrice());
        assertEquals(10, product1.getQuantity());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should update only description field when others are null")
    void testUpdate_PartialUpdate_DescriptionOnly() {
        Product partialUpdate = Product.builder()
                .description("New description")
                .build();

        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));

        productService.update(2L, partialUpdate);

        assertEquals("Mouse", product2.getName());
        assertEquals("New description", product2.getDescription());
        assertEquals(29.99, product2.getPrice());
        verify(productRepository, times(1)).findById(2L);
    }

    @Test
    @DisplayName("Should update only quantity field when others are zero or null")
    void testUpdate_PartialUpdate_QuantityOnly() {
        Product partialUpdate = new Product();
        partialUpdate.setQuantity(100);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        productService.update(1L, partialUpdate);

        assertEquals("Laptop", product1.getName());
        assertEquals("High performance laptop", product1.getDescription());
        assertEquals(999.99, product1.getPrice());
        assertEquals(100, product1.getQuantity());
    }

    @Test
    @DisplayName("Should not update when product not found")
    void testUpdate_NotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> productService.update(999L, product1));

        assertEquals("Product not found with id: 999", exception.getMessage());
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should not update fields when update data has null values")
    void testUpdate_WithNullFields() {
        Product updateData = new Product();
        updateData.setId(null);
        updateData.setName(null);
        updateData.setDescription(null);
        updateData.setPrice(null);
        updateData.setQuantity(0);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        productService.update(1L, updateData);

        assertEquals("Laptop", product1.getName());
        assertEquals("High performance laptop", product1.getDescription());
        assertEquals(999.99, product1.getPrice());
        assertEquals(10, product1.getQuantity());
    }

    @Test
    @DisplayName("Should handle updating to zero quantity")
    void testUpdate_ZeroQuantity() {
        Product updateData = Product.builder()
                .quantity(0)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));

        productService.update(1L, updateData);

        assertEquals(10, product1.getQuantity()); // Should not update to 0
    }

    // ==================== Tests for deleteById() ====================

    @Test
    @DisplayName("Should delete product by ID successfully")
    void testDeleteById_Success() {
        productService.deleteById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should handle deletion of non-existent product")
    void testDeleteById_NonExistent() {
        productService.deleteById(999L);
        verify(productRepository, times(1)).deleteById(999L);
    }

    @Test
    @DisplayName("Should delete multiple products")
    void testDeleteById_Multiple() {
        productService.deleteById(1L);
        productService.deleteById(2L);
        productService.deleteById(3L);

        verify(productRepository, times(1)).deleteById(1L);
        verify(productRepository, times(1)).deleteById(2L);
        verify(productRepository, times(1)).deleteById(3L);
    }

    // ==================== Integration Tests ====================

    @Test
    @DisplayName("Should perform full CRUD operations")
    void testFullCRUDOperations() {
        // Create
        when(productRepository.save(any(Product.class))).thenReturn(product1);
        productService.save(product1);
        verify(productRepository, times(1)).save(product1);

        // Read
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        Product retrieved = productService.findById(1L);
        assertEquals(product1, retrieved);

        // Update
        Product updateData = Product.builder().name("Updated").build();
        productService.update(1L, updateData);
        assertEquals("Updated", product1.getName());

        // Delete
        productService.deleteById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }
}

