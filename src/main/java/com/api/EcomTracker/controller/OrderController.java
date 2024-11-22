package com.api.EcomTracker.controller;

import com.api.EcomTracker.domain.order.OrderRequestDTO;
import com.api.EcomTracker.domain.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody @Valid OrderRequestDTO orderRequest) {
        return orderService.createOrder(orderRequest);
    }
}