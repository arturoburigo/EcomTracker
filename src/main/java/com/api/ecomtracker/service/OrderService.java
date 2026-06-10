package com.api.ecomtracker.service;

import com.api.ecomtracker.domain.Order;
import com.api.ecomtracker.domain.Product;
import com.api.ecomtracker.domain.User;
import com.api.ecomtracker.dto.order.CheckoutResponse;
import com.api.ecomtracker.dto.order.OrderRequest;
import com.api.ecomtracker.exception.BusinessException;
import com.api.ecomtracker.exception.ResourceNotFoundException;
import com.api.ecomtracker.repository.OrderRepository;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final ProductService productService;

    private final StripeCheckoutService stripeCheckoutService;

    public OrderService(
            OrderRepository orderRepository,
            ProductService productService,
            StripeCheckoutService stripeCheckoutService) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.stripeCheckoutService = stripeCheckoutService;
    }

    @Transactional
    public CheckoutResponse checkout(User customer, OrderRequest request) {
        Product product = productService.findById(request.getProductId());
        if (!product.hasStockFor(request.getQuantity())) {
            throw new BusinessException(
                    String.format("Insufficient stock. Available quantity: %d", product.getQuantity()));
        }

        product.decreaseStock(request.getQuantity());

        BigDecimal totalPrice = calculateTotalPrice(product, request.getQuantity());
        Order order = orderRepository.save(new Order(product, customer, totalPrice, request.getQuantity()));

        String checkoutUrl = stripeCheckoutService.createCheckoutSession(order);
        return new CheckoutResponse(order.getId(), checkoutUrl);
    }

    @Transactional
    public void markAsPaid(Long orderId) {
        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        order.markAsPaid();
        orderRepository.save(order);
    }

    private BigDecimal calculateTotalPrice(Product product, int quantity) {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
