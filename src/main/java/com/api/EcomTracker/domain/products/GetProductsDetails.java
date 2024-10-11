package com.api.EcomTracker.domain.products;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class GetProductsDetails {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private String description;
    private String color;
    private String size;
    private Long categoryId;
    private String categoryName;

    public GetProductsDetails(Products products){
        this.id = products.getId();
        this.name = products.getName();
        this.price = products.getPrice();
        this.quantity = products.getQuantity();
        this.description = products.getDescription();
        this.color =products.getColor();
        this.size = products.getSize();
        this.categoryId = products.getCategory().getId();
        this.categoryName = products.getCategory().getName();
    }
}
