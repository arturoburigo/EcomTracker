package com.api.EcomTracker.domain.order;

import com.api.EcomTracker.domain.products.Products;
import com.api.EcomTracker.domain.users.Users;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "orders")
@Entity(name = "Orders")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "product_id")
    @Column(nullable = false)
    private Products products;

    @ManyToOne
    @Column(nullable = false)
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Integer quantity;


}
