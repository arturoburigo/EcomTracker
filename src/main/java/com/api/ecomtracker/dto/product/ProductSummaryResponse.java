package com.api.ecomtracker.dto.product;

import com.api.ecomtracker.domain.Product;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class ProductSummaryResponse {

    private final Long id;

    private final String name;

    private final BigDecimal price;

    private final Integer quantity;

    private final String categoryName;

    private final Boolean active;

    public ProductSummaryResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.quantity = product.getQuantity();
        this.categoryName = product.getCategory().getName();
        this.active = product.getActive();
    }
}
