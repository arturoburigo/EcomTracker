package com.api.ecomtracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.api.ecomtracker.dto.order.CheckoutResponse;
import com.api.ecomtracker.dto.order.OrderRequest;
import com.api.ecomtracker.exception.BusinessException;
import com.api.ecomtracker.exception.GlobalExceptionHandler;
import com.api.ecomtracker.service.OrderService;
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
@DisplayName("OrderController")
class OrderControllerTest {

    @Mock private OrderService orderService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc =
                MockMvcBuilders.standaloneSetup(new OrderController(orderService))
                        .setControllerAdvice(new GlobalExceptionHandler())
                        .build();
    }

    @Test
    @DisplayName("POST /orders should return 200 with the checkout URL")
    void createOrderShouldReturnCheckoutUrl() throws Exception {
        when(orderService.checkout(any(), any(OrderRequest.class)))
                .thenReturn(new CheckoutResponse(1L, "https://checkout.stripe.com/session"));

        mockMvc.perform(
                        post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"productId\": 1, \"quantity\": 2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.checkoutUrl").value("https://checkout.stripe.com/session"));
    }

    @Test
    @DisplayName("POST /orders should return 400 when stock is insufficient")
    void createOrderShouldReturnBadRequestForInsufficientStock() throws Exception {
        when(orderService.checkout(any(), any(OrderRequest.class)))
                .thenThrow(new BusinessException("Insufficient stock. Available quantity: 1"));

        mockMvc.perform(
                        post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"productId\": 1, \"quantity\": 2}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Business rule violation"));
    }

    @Test
    @DisplayName("POST /orders should return 400 when quantity is not positive")
    void createOrderShouldReturnBadRequestForInvalidQuantity() throws Exception {
        mockMvc.perform(
                        post("/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"productId\": 1, \"quantity\": 0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }
}
