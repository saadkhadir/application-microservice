package org.example.productservice.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.productservice.entity.Product;
import org.example.productservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

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

    // ==================== Tests for GET /products ====================

    @Test
    @DisplayName("Should return all products with status 200")
    void testFindAll_Success() throws Exception {
        List<Product> products = Arrays.asList(product1, product2, product3);
        when(productService.findAll()).thenReturn(products);

        mockMvc.perform(get("/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[0].price").value(999.99))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Mouse"))
                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].name").value("Keyboard"));

        verify(productService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void testFindAll_Empty() throws Exception {
        when(productService.findAll()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(productService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return single product in list")
    void testFindAll_SingleProduct() throws Exception {
        when(productService.findAll()).thenReturn(Arrays.asList(product1));

        mockMvc.perform(get("/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("Laptop"));

        verify(productService, times(1)).findAll();
    }

    // ==================== Tests for GET /products/{id} ====================

    @Test
    @DisplayName("Should return product by ID with status 200")
    void testFindById_Success() throws Exception {
        when(productService.findById(1L)).thenReturn(product1);

        mockMvc.perform(get("/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.description").value("High performance laptop"))
                .andExpect(jsonPath("$.price").value(999.99))
                .andExpect(jsonPath("$.quantity").value(10));

        verify(productService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return 500 when product not found")
    void testFindById_NotFound() throws Exception {
        when(productService.findById(999L))
                .thenThrow(new RuntimeException("Product not found with id: 999"));

        mockMvc.perform(get("/products/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is5xxServerError());

        verify(productService, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should return different products for different IDs")
    void testFindById_MultipleIds() throws Exception {
        when(productService.findById(1L)).thenReturn(product1);
        when(productService.findById(2L)).thenReturn(product2);

        mockMvc.perform(get("/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));

        mockMvc.perform(get("/products/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mouse"));

        verify(productService, times(1)).findById(1L);
        verify(productService, times(1)).findById(2L);
    }

    // ==================== Tests for POST /products ====================

    @Test
    @DisplayName("Should create product successfully")
    void testSave_Success() throws Exception {
        Product newProduct = Product.builder()
                .name("Monitor")
                .description("4K Monitor")
                .price(399.99)
                .quantity(15)
                .build();

        doNothing().when(productService).save(any(Product.class));

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isOk());

        verify(productService, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should save product with all fields")
    void testSave_WithAllFields() throws Exception {
        Product newProduct = Product.builder()
                .name("Speaker")
                .description("Bluetooth speaker")
                .price(149.99)
                .quantity(30)
                .build();

        doNothing().when(productService).save(any(Product.class));

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isOk());

        verify(productService, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should handle empty product in POST request")
    void testSave_EmptyProduct() throws Exception {
        Product emptyProduct = new Product();

        doNothing().when(productService).save(any(Product.class));

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyProduct)))
                .andExpect(status().isOk());

        verify(productService, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should handle invalid JSON in POST request")
    void testSave_InvalidJson() throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid json}"))
                .andExpect(status().is4xxClientError());
    }

    // ==================== Tests for PUT /products/{id} ====================

    @Test
    @DisplayName("Should update product successfully")
    void testUpdate_Success() throws Exception {
        Product updateData = Product.builder()
                .name("Updated Laptop")
                .description("Updated description")
                .price(1199.99)
                .quantity(15)
                .build();

        doNothing().when(productService).update(eq(1L), any(Product.class));

        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk());

        verify(productService, times(1)).update(eq(1L), any(Product.class));
    }

    @Test
    @DisplayName("Should update only one field")
    void testUpdate_PartialUpdate() throws Exception {
        Product updateData = Product.builder()
                .name("New Name")
                .build();

        doNothing().when(productService).update(eq(1L), any(Product.class));

        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk());

        verify(productService, times(1)).update(eq(1L), any(Product.class));
    }

    @Test
    @DisplayName("Should return 500 when product to update not found")
    void testUpdate_NotFound() throws Exception {
        Product updateData = Product.builder()
                .name("Updated")
                .build();

        doThrow(new RuntimeException("Product not found with id: 999"))
                .when(productService).update(eq(999L), any(Product.class));

        mockMvc.perform(put("/products/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().is5xxServerError());

        verify(productService, times(1)).update(eq(999L), any(Product.class));
    }

    @Test
    @DisplayName("Should update with empty data")
    void testUpdate_EmptyData() throws Exception {
        Product emptyData = new Product();

        doNothing().when(productService).update(eq(1L), any(Product.class));

        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyData)))
                .andExpect(status().isOk());

        verify(productService, times(1)).update(eq(1L), any(Product.class));
    }

    @Test
    @DisplayName("Should update multiple products")
    void testUpdate_Multiple() throws Exception {
        Product updateData = Product.builder().name("Updated").build();

        doNothing().when(productService).update(anyLong(), any(Product.class));

        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/products/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk());

        verify(productService, times(2)).update(anyLong(), any(Product.class));
    }

    // ==================== Tests for DELETE /products/{id} ====================

    @Test
    @DisplayName("Should delete product successfully")
    void testDeleteById_Success() throws Exception {
        doNothing().when(productService).deleteById(1L);

        mockMvc.perform(delete("/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productService, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should handle deletion of non-existent product")
    void testDeleteById_NonExistent() throws Exception {
        doNothing().when(productService).deleteById(999L);

        mockMvc.perform(delete("/products/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productService, times(1)).deleteById(999L);
    }

    @Test
    @DisplayName("Should delete multiple products")
    void testDeleteById_Multiple() throws Exception {
        doNothing().when(productService).deleteById(anyLong());

        mockMvc.perform(delete("/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/products/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/products/3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productService, times(3)).deleteById(anyLong());
    }

    // ==================== Integration Tests ====================

    @Test
    @DisplayName("Should perform full REST API workflow")
    void testFullWorkflow() throws Exception {
        Product newProduct = Product.builder()
                .name("Webcam")
                .description("HD Webcam")
                .price(89.99)
                .quantity(20)
                .build();

        // Create
        doNothing().when(productService).save(any(Product.class));
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isOk());

        // Read
        when(productService.findById(1L)).thenReturn(product1);
        mockMvc.perform(get("/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"));

        // Update
        Product updateData = Product.builder().name("Updated").build();
        doNothing().when(productService).update(eq(1L), any(Product.class));
        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk());

        // Delete
        doNothing().when(productService).deleteById(1L);
        mockMvc.perform(delete("/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle concurrent requests properly")
    void testConcurrentRequests() throws Exception {
        when(productService.findAll()).thenReturn(Arrays.asList(product1, product2));
        when(productService.findById(1L)).thenReturn(product1);
        when(productService.findById(2L)).thenReturn(product2);

        mockMvc.perform(get("/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(get("/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/products/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(productService, times(1)).findAll();
        verify(productService, times(1)).findById(1L);
        verify(productService, times(1)).findById(2L);
    }
}

