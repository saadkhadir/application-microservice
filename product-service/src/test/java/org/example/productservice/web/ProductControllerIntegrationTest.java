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
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("ProductController Integration Tests")
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .description("Test Description")
                .price(99.99)
                .quantity(10)
                .build();
    }

    // ==================== Content Type Tests ====================

    @Test
    @DisplayName("Should handle JSON content type correctly")
    void testContentTypeJSON() throws Exception {
        when(productService.findById(1L)).thenReturn(product);

        mockMvc.perform(get("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // ==================== Response Header Tests ====================

    @Test
    @DisplayName("Should return correct response headers")
    void testResponseHeaders() throws Exception {
        when(productService.findAll()).thenReturn(Arrays.asList(product));

        mockMvc.perform(get("/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // ==================== Error Handling Tests ====================

    @Test
    @DisplayName("Should handle bad request with malformed data")
    void testBadRequest_MalformedData() throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\": \"invalid\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("Should return proper error response format")
    void testErrorResponseFormat() throws Exception {
        when(productService.findById(999L))
                .thenThrow(new RuntimeException("Product not found"));

        mockMvc.perform(get("/products/999"))
                .andExpect(status().is5xxServerError());
    }

    // ==================== Path Variable Tests ====================

    @Test
    @DisplayName("Should handle valid numeric path variables")
    void testValidNumericPathVariable() throws Exception {
        when(productService.findById(1L)).thenReturn(product);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle large numeric path variables")
    void testLargeNumericPathVariable() throws Exception {
        when(productService.findById(9999999L)).thenReturn(product);

        mockMvc.perform(get("/products/9999999"))
                .andExpect(status().isOk());
    }

    // ==================== Request Body Tests ====================

    @Test
    @DisplayName("Should handle request body with special characters")
    void testRequestBodyWithSpecialCharacters() throws Exception {
        Product specialProduct = Product.builder()
                .name("Product with @#$%^&*()")
                .description("Description with special chars: äöü")
                .price(99.99)
                .quantity(10)
                .build();

        doNothing().when(productService).save(any(Product.class));

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialProduct)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle very long product names")
    void testLongProductName() throws Exception {
        String longName = "A".repeat(500);
        Product longProduct = Product.builder()
                .name(longName)
                .description("Description")
                .price(99.99)
                .quantity(10)
                .build();

        doNothing().when(productService).save(any(Product.class));

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longProduct)))
                .andExpect(status().isOk());
    }

    // ==================== Edge Case Tests ====================

    @Test
    @DisplayName("Should handle zero price product")
    void testZeroPriceProduct() throws Exception {
        Product freeProduct = Product.builder()
                .name("Free Product")
                .description("No cost")
                .price(0.0)
                .quantity(10)
                .build();

        doNothing().when(productService).save(any(Product.class));

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(freeProduct)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle negative price product")
    void testNegativePriceProduct() throws Exception {
        Product negativeProduct = Product.builder()
                .name("Discount Product")
                .description("With discount")
                .price(-99.99)
                .quantity(10)
                .build();

        doNothing().when(productService).save(any(Product.class));

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(negativeProduct)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle zero quantity product")
    void testZeroQuantityProduct() throws Exception {
        Product outOfStock = Product.builder()
                .name("Out of Stock")
                .description("Not available")
                .price(99.99)
                .quantity(0)
                .build();

        doNothing().when(productService).save(any(Product.class));

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(outOfStock)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle very large quantity")
    void testLargeQuantityProduct() throws Exception {
        Product largeQtyProduct = Product.builder()
                .name("Bulk Product")
                .description("Large quantity")
                .price(99.99)
                .quantity(999999)
                .build();

        doNothing().when(productService).save(any(Product.class));

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(largeQtyProduct)))
                .andExpect(status().isOk());
    }

    // ==================== Response Body Validation Tests ====================

    @Test
    @DisplayName("Should return all product fields in response")
    void testResponseBodyContainsAllFields() throws Exception {
        when(productService.findById(1L)).thenReturn(product);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.description").exists())
                .andExpect(jsonPath("$.price").exists())
                .andExpect(jsonPath("$.quantity").exists());
    }

    @Test
    @DisplayName("Should return correct field values in response")
    void testResponseBodyFieldValues() throws Exception {
        when(productService.findById(1L)).thenReturn(product);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.description", is("Test Description")))
                .andExpect(jsonPath("$.price", is(99.99)))
                .andExpect(jsonPath("$.quantity", is(10)));
    }

    @Test
    @DisplayName("Should return multiple products with correct format")
    void testMultipleProductsFormat() throws Exception {
        List<Product> products = Arrays.asList(product);
        when(productService.findAll()).thenReturn(products);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$", isA(List.class)))
                .andExpect(jsonPath("$[0]", hasKey("id")))
                .andExpect(jsonPath("$[0]", hasKey("name")))
                .andExpect(jsonPath("$[0]", hasKey("price")));
    }

    // ==================== Method Tests ====================

    @Test
    @DisplayName("Should support GET method for findAll")
    void testGetMethodForFindAll() throws Exception {
        when(productService.findAll()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should support GET method for findById")
    void testGetMethodForFindById() throws Exception {
        when(productService.findById(1L)).thenReturn(product);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should support POST method for save")
    void testPostMethodForSave() throws Exception {
        doNothing().when(productService).save(any(Product.class));

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should support PUT method for update")
    void testPutMethodForUpdate() throws Exception {
        doNothing().when(productService).update(any(Long.class), any(Product.class));

        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should support DELETE method")
    void testDeleteMethod() throws Exception {
        doNothing().when(productService).deleteById(1L);

        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isOk());
    }
}

