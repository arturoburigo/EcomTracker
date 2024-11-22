package com.api.EcomTracker.domain.order;

import com.api.EcomTracker.domain.products.Products;
import com.api.EcomTracker.domain.users.Users;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "orders")
@Entity(name = "Orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Products products;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(name = "total_price", nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatusDTO status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Orders(Products products, Users user, BigDecimal amount, Integer quantity, OrderStatusDTO status) {
        this.products = products;
        this.user = user;
        this.amount = amount;
        this.quantity = quantity;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }
}