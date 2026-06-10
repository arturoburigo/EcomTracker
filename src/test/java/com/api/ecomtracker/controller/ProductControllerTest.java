package com.api.ecomtracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.api.ecomtracker.domain.Category;
import com.api.ecomtracker.domain.Product;
import com.api.ecomtracker.dto.product.ProductRequest;
import com.api.ecomtracker.exception.GlobalExceptionHandler;
import com.api.ecomtracker.exception.ResourceNotFoundException;
import com.api.ecomtracker.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductController")
class ProductControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private ProductService productService;

    private MockMvc mockMvc;

    private Product product;

    @BeforeEach
    void setUp() {
        mockMvc =
                MockMvcBuilders.standaloneSetup(new ProductController(productService))
                        .setControllerAdvice(new GlobalExceptionHandler())
                        .build();
        product =
                new Product(
                        1L,
                        "Sneaker",
                        "Running shoes",
                        "Black",
                        "42",
                        BigDecimal.TEN,
                        5,
                        true,
                        new Category(1L, "Shoes", true));
    }

    @Test
    @DisplayName("POST /products should return 201 with Location header")
    void registerShouldReturnCreated() throws Exception {
        when(productService.register(any(ProductRequest.class))).thenReturn(product);

        ProductRequest request = new ProductRequest();
        request.setName("Sneaker");
        request.setPrice(BigDecimal.TEN);
        request.setQuantity(5);
        request.setCategoryId(1L);

        mockMvc.perform(
                        post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/products/1"))
                .andExpect(jsonPath("$.name").value("Sneaker"));
    }

    @Test
    @DisplayName("POST /products should return 400 when the body is invalid")
    void registerShouldReturnBadRequestForInvalidBody() throws Exception {
        mockMvc.perform(post("/products").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    @DisplayName("GET /products/{id} should return 200 with the product")
    void getProductShouldReturnProduct() throws Exception {
        when(productService.findById(1L)).thenReturn(product);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.categoryName").value("Shoes"));
    }

    @Test
    @DisplayName("GET /products/{id} should return 404 when the product does not exist")
    void getProductShouldReturnNotFound() throws Exception {
        when(productService.findById(99L)).thenThrow(new ResourceNotFoundException("Product", 99L));

        mockMvc.perform(get("/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource not found"));
    }

    @Test
    @DisplayName("PUT /products/{id} should return 200 with the updated product")
    void updateQuantityShouldReturnUpdatedProduct() throws Exception {
        when(productService.updateQuantity(eq(1L), eq(8))).thenReturn(product);

        mockMvc.perform(
                        put("/products/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"quantity\": 8}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /products/{id} should return 400 when quantity is missing")
    void updateQuantityShouldReturnBadRequestWhenMissing() throws Exception {
        mockMvc.perform(put("/products/1").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /products/{id} should return 204")
    void deleteShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/products/1")).andExpect(status().isNoContent());
    }
}
