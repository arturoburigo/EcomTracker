package com.api.ecomtracker.dto.product;

import com.api.ecomtracker.domain.Product;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class ProductResponse {

    private final Long id;

    private final String name;

    private final BigDecimal price;

    private final Integer quantity;

    private final String description;

    private final String color;

    private final String size;

    private final Long categoryId;

    private final String categoryName;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.quantity = product.getQuantity();
        this.description = product.getDescription();
        this.color = product.getColor();
        this.size = product.getSize();
        this.categoryId = product.getCategory().getId();
        this.categoryName = product.getCategory().getName();
    }
}
