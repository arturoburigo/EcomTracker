package com.api.ecomtracker.controller;

import com.api.ecomtracker.domain.User;
import com.api.ecomtracker.dto.order.CheckoutResponse;
import com.api.ecomtracker.dto.order.OrderRequest;
import com.api.ecomtracker.service.OrderService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<CheckoutResponse> createOrder(
            @AuthenticationPrincipal User customer, @RequestBody @Valid OrderRequest request) {
        return ResponseEntity.ok(orderService.checkout(customer, request));
    }
}
