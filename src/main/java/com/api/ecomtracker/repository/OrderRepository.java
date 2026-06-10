package com.api.ecomtracker.repository;

import com.api.ecomtracker.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {}
