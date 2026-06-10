package com.api.ecomtracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.api.ecomtracker.domain.Category;
import com.api.ecomtracker.domain.Order;
import com.api.ecomtracker.domain.OrderStatus;
import com.api.ecomtracker.domain.Product;
import com.api.ecomtracker.domain.Role;
import com.api.ecomtracker.domain.User;
import com.api.ecomtracker.dto.order.CheckoutResponse;
import com.api.ecomtracker.dto.order.OrderRequest;
import com.api.ecomtracker.exception.BusinessException;
import com.api.ecomtracker.exception.ResourceNotFoundException;
import com.api.ecomtracker.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService")
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;

    @Mock private ProductService productService;

    @Mock private StripeCheckoutService stripeCheckoutService;

    @InjectMocks private OrderService orderService;

    private User customer;

    private Product product;

    private OrderRequest request;

    @BeforeEach
    void setUp() {
        customer =
                new User(
                        "customer@example.com",
                        "customer",
                        "encoded",
                        new Role(1L, Role.RoleName.USER));
        product =
                new Product(
                        1L,
                        "Sneaker",
                        "Running shoes",
                        "Black",
                        "42",
                        new BigDecimal("50.00"),
                        10,
                        true,
                        new Category(1L, "Shoes", true));
        request = new OrderRequest();
        request.setProductId(1L);
        request.setQuantity(2);
    }

    @Test
    @DisplayName("checkout should decrease stock, save a pending order and return the checkout URL")
    void checkoutShouldCreateOrderAndReturnCheckoutUrl() {
        when(productService.findById(1L)).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(call -> call.getArgument(0));
        when(stripeCheckoutService.createCheckoutSession(any(Order.class)))
                .thenReturn("https://checkout.stripe.com/session");

        CheckoutResponse response = orderService.checkout(customer, request);

        assertThat(response.getCheckoutUrl()).isEqualTo("https://checkout.stripe.com/session");
        assertThat(product.getQuantity()).isEqualTo(8);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    @DisplayName("checkout should compute the total price as price times quantity")
    void checkoutShouldComputeTotalPrice() {
        when(productService.findById(1L)).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenAnswer(call -> call.getArgument(0));
        when(stripeCheckoutService.createCheckoutSession(any(Order.class))).thenReturn("url");

        orderService.checkout(customer, request);

        verify(orderRepository)
                .save(
                        org.mockito.ArgumentMatchers.argThat(
                                order ->
                                        order.getTotalPrice().compareTo(new BigDecimal("100.00"))
                                                        == 0
                                                && order.getStatus() == OrderStatus.PENDING));
    }

    @Test
    @DisplayName("checkout should reject orders with insufficient stock")
    void checkoutShouldRejectInsufficientStock() {
        request.setQuantity(99);
        when(productService.findById(1L)).thenReturn(product);

        assertThatThrownBy(() -> orderService.checkout(customer, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Insufficient stock");
        verify(orderRepository, never()).save(any(Order.class));
        assertThat(product.getQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("markAsPaid should change the order status to PAID")
    void markAsPaidShouldUpdateStatus() {
        Order order = new Order(product, customer, new BigDecimal("100.00"), 2);
        when(orderRepository.findById(7L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(call -> call.getArgument(0));

        orderService.markAsPaid(7L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    @DisplayName("markAsPaid should throw ResourceNotFoundException for unknown orders")
    void markAsPaidShouldThrowWhenOrderMissing() {
        when(orderRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.markAsPaid(7L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order");
    }
}
