package com.api.ecomtracker.domain;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    private String color;

    private String size;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    private Boolean active;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Product(
            String name,
            String description,
            String color,
            String size,
            BigDecimal price,
            Integer quantity,
            Category category) {
        this.name = name;
        this.description = description;
        this.color = color;
        this.size = size;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.active = true;
    }

    public boolean hasStockFor(int requestedQuantity) {
        return quantity >= requestedQuantity;
    }

    public void decreaseStock(int amount) {
        updateQuantity(quantity - amount);
    }

    public void updateQuantity(Integer newQuantity) {
        if (newQuantity == null || newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = newQuantity;
    }

    public void deactivate() {
        this.active = false;
    }
}
